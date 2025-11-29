import { useState } from 'react';
import { Navigation } from '../components/Navigation';
import { TestConfigPanel } from '../components/TestConfigPanel';
import { ScenarioBuilder } from '../components/ScenarioBuilder';
import { TestSummaryPanel } from '../components/TestSummaryPanel';
import { TestConfig, TestStep, TestMetrics } from '../types/test';
import { useNavigate } from 'react-router';

export function CreateTestPage() {
  const navigate = useNavigate();
  const [config, setConfig] = useState<TestConfig>({
    url: '',
    method: 'GET',
    testType: 'fixed',
    virtualUsers: 10,
    duration: 60,
    rampUpTime: 10,
    headers: [],
    body: '',
    authType: 'none',
    timeout: 30,
    keepAlive: true,
    connectionReuse: true,
    delayBetweenRequests: 0,
  });

  const [steps, setSteps] = useState<TestStep[]>([]);
  const [isRunning, setIsRunning] = useState(false);
  const [metrics, setMetrics] = useState<TestMetrics | null>(null);
  const [responseTimeData, setResponseTimeData] = useState<Array<{ time: number; responseTime: number }>>([]);
  const [activeUsersData, setActiveUsersData] = useState<Array<{ time: number; users: number; rps: number }>>([]);

  const calculateActiveUsers = (elapsedSeconds: number): number => {
    const { testType, virtualUsers, duration, rampUpTime } = config;
    
    switch (testType) {
      case 'fixed':
        return virtualUsers;
      
      case 'ramp-up':
        if (elapsedSeconds <= rampUpTime) {
          return Math.floor((elapsedSeconds / rampUpTime) * virtualUsers);
        }
        return virtualUsers;
      
      case 'spike':
        const spikePoint = duration / 2;
        if (elapsedSeconds < spikePoint - 5) {
          return Math.floor(virtualUsers * 0.3);
        } else if (elapsedSeconds >= spikePoint - 5 && elapsedSeconds < spikePoint + 5) {
          return virtualUsers;
        } else {
          return Math.floor(virtualUsers * 0.3);
        }
      
      case 'stress':
        const stepDuration = duration / 4;
        const currentStep = Math.floor(elapsedSeconds / stepDuration);
        return Math.floor(virtualUsers * (0.25 * (currentStep + 1)));
      
      default:
        return virtualUsers;
    }
  };

  const handleStartTest = () => {
    if (!config.url && steps.length === 0) {
      alert('Please enter a URL or add test steps');
      return;
    }

    setIsRunning(true);
    setMetrics({
      totalRequests: 0,
      successfulRequests: 0,
      failedRequests: 0,
      averageResponseTime: 0,
      minResponseTime: 0,
      maxResponseTime: 0,
      p50ResponseTime: 0,
      p95ResponseTime: 0,
      p99ResponseTime: 0,
      requestsPerSecond: 0,
      errorRate: 0,
    });
    setResponseTimeData([]);
    setActiveUsersData([]);

    let elapsedSeconds = 0;

    const interval = setInterval(() => {
      elapsedSeconds++;
      const currentUsers = calculateActiveUsers(elapsedSeconds);
      
      setMetrics((prev) => {
        if (!prev) return prev;
        
        const requestsThisSecond = Math.floor(currentUsers * (Math.random() * 2 + 1));
        const newTotal = prev.totalRequests + requestsThisSecond;
        const newFailed = prev.failedRequests + Math.floor(Math.random() * (currentUsers / 20));
        const newSuccessful = newTotal - newFailed;
        const baseResponseTime = 100 + (currentUsers / config.virtualUsers) * 200;
        const newAvg = Math.floor(baseResponseTime + Math.random() * 100);
        const newMin = Math.min(prev.minResponseTime || newAvg, newAvg - Math.floor(Math.random() * 50));
        const newMax = Math.max(prev.maxResponseTime, newAvg + Math.floor(Math.random() * 200));

        return {
          totalRequests: newTotal,
          successfulRequests: newSuccessful,
          failedRequests: newFailed,
          averageResponseTime: newAvg,
          minResponseTime: newMin,
          maxResponseTime: newMax,
          p50ResponseTime: Math.floor(newAvg * 0.9),
          p95ResponseTime: Math.floor(newAvg * 1.5),
          p99ResponseTime: Math.floor(newAvg * 2),
          requestsPerSecond: requestsThisSecond,
          errorRate: (newFailed / newTotal) * 100,
        };
      });

      setResponseTimeData((prev) => {
        const baseResponseTime = 100 + (currentUsers / config.virtualUsers) * 200;
        const newData = [
          ...prev,
          {
            time: elapsedSeconds,
            responseTime: Math.floor(baseResponseTime + Math.random() * 100),
          },
        ];
        return newData.slice(-60);
      });

      setActiveUsersData((prev) => {
        const newData = [
          ...prev,
          {
            time: elapsedSeconds,
            users: currentUsers,
            rps: Math.floor(currentUsers * (Math.random() * 2 + 1)),
          },
        ];
        return newData.slice(-60);
      });
    }, 1000);

    setTimeout(() => {
      clearInterval(interval);
      setIsRunning(false);
      
      const testId = Date.now().toString();
      const startTime = new Date(Date.now() - config.duration * 1000);
      const endTime = new Date();
      
      const testResult: TestResult = {
        id: testId,
        name: `Test - ${endTime.toLocaleString()}`,
        config,
        steps,
        metrics: metrics || {
          totalRequests: 0,
          successfulRequests: 0,
          failedRequests: 0,
          averageResponseTime: 0,
          minResponseTime: 0,
          maxResponseTime: 0,
          p50ResponseTime: 0,
          p95ResponseTime: 0,
          p99ResponseTime: 0,
          requestsPerSecond: 0,
          errorRate: 0,
        },
        responseTimeData: responseTimeData || [],
        activeUsersData: activeUsersData || [],
        startTime,
        endTime,
        status: 'completed' as const,
      };
      
      try {
        const savedTests = JSON.parse(localStorage.getItem('testHistory') || '[]');
        savedTests.unshift(testResult);
        localStorage.setItem('testHistory', JSON.stringify(savedTests.slice(0, 20)));
        console.log('Test saved:', testId, testResult);
      } catch (error) {
        console.error('Error saving test:', error);
      }
      
      navigate(`/results/${testId}`);
    }, config.duration * 1000);
  };

  const handleStopTest = () => {
    setIsRunning(false);
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 transition-colors">
      <Navigation />

      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8">
          <h1 className="text-slate-900 dark:text-slate-100 mb-2">Create Load Test</h1>
          <p className="text-slate-600 dark:text-slate-400">Configure your test parameters and start testing</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-1 space-y-6">
            <TestConfigPanel
              config={config}
              onConfigChange={setConfig}
              disabled={isRunning}
            />
            <ScenarioBuilder
              steps={steps}
              onStepsChange={setSteps}
              disabled={isRunning}
            />
          </div>

          <div className="lg:col-span-2">
            <TestSummaryPanel
              config={config}
              steps={steps}
              isRunning={isRunning}
              metrics={metrics}
              responseTimeData={responseTimeData}
              activeUsersData={activeUsersData}
              onStart={handleStartTest}
              onStop={handleStopTest}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
