import { Navigation } from '../components/Navigation';
import { Card, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { useNavigate } from 'react-router';
import { Clock, Users, Zap, ChevronRight, Trash2 } from 'lucide-react';
import { useEffect, useState } from 'react';
import { TestResult } from '../types/test';

export function HistoryPage() {
  const navigate = useNavigate();
  const [tests, setTests] = useState<TestResult[]>([]);

  useEffect(() => {
    try {
      const savedTests = JSON.parse(localStorage.getItem('testHistory') || '[]');
      // Filter and normalize tests - be more lenient with validation
      const validTests = savedTests
        .filter(
          (test: any) =>
            test &&
            test.id &&
            (test.name || test.id) // Allow tests without name, use id as fallback
        )
        .map((test: any) => {
          // Normalize dates if they're strings
          if (test.startTime && typeof test.startTime === 'string') {
            test.startTime = new Date(test.startTime);
          }
          if (test.endTime && typeof test.endTime === 'string') {
            test.endTime = new Date(test.endTime);
          }
          // Ensure metrics exist with defaults
          if (!test.metrics) {
            test.metrics = {
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
            };
          }
          // Ensure config exists with defaults
          if (!test.config) {
            test.config = {
              url: '',
              method: 'GET',
              testType: 'fixed',
              virtualUsers: 0,
              duration: 0,
              rampUpTime: 0,
              headers: [],
              body: '',
              authType: 'none',
              timeout: 30,
              keepAlive: true,
              connectionReuse: true,
              delayBetweenRequests: 0,
            };
          }
          // Ensure arrays exist
          if (!test.responseTimeData) test.responseTimeData = [];
          if (!test.activeUsersData) test.activeUsersData = [];
          // Ensure name exists
          if (!test.name) {
            test.name = `Test ${test.id}`;
          }
          return test;
        });
      setTests(validTests);
      // Update localStorage with normalized data
      localStorage.setItem('testHistory', JSON.stringify(validTests));
    } catch (error) {
      console.error('Error loading test history:', error);
      setTests([]);
    }
  }, []);

  const deleteTest = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    const updatedTests = tests.filter((t) => t.id !== id);
    setTests(updatedTests);
    localStorage.setItem('testHistory', JSON.stringify(updatedTests));
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 transition-colors">
      <Navigation />

      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8">
          <h1 className="text-slate-900 dark:text-slate-100 mb-2">Test History</h1>
          <p className="text-slate-600 dark:text-slate-400">View and manage your past load tests</p>
        </div>

        {tests.length === 0 ? (
          <Card className="shadow-sm border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
            <CardContent className="py-16 text-center">
              <Clock className="size-16 mx-auto mb-4 text-slate-300 dark:text-slate-700" />
              <p className="text-slate-600 dark:text-slate-400 mb-6">No test history yet</p>
              <Button onClick={() => navigate('/create')}>Create Your First Test</Button>
            </CardContent>
          </Card>
        ) : (
          <div className="space-y-4">
            {tests.map((test) => (
              <Card
                key={test.id}
                className="shadow-sm hover:shadow-md transition-shadow cursor-pointer border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900"
                onClick={() => navigate(`/results/${test.id}`)}
              >
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <h3 className="text-slate-900 dark:text-slate-100 mb-2">{test.name}</h3>
                      <div className="flex items-center gap-6 text-sm text-slate-600 dark:text-slate-400">
                        <div className="flex items-center gap-2">
                          <Clock className="size-4" />
                          {test.startTime
                            ? (test.startTime instanceof Date
                                ? test.startTime
                                : new Date(test.startTime)
                              ).toLocaleString()
                            : 'Unknown date'}
                        </div>
                        {test.config && (
                          <>
                            <div className="flex items-center gap-2">
                              <Users className="size-4" />
                              {test.config.virtualUsers || 0} users
                            </div>
                            <div className="flex items-center gap-2">
                              <Zap className="size-4" />
                              {test.config.testType || 'unknown'}
                            </div>
                          </>
                        )}
                      </div>
                      {test.metrics && (
                        <div className="flex items-center gap-6 mt-3 text-sm">
                          <div>
                            <span className="text-slate-600 dark:text-slate-400">Requests: </span>
                            <span className="text-slate-900 dark:text-slate-100">
                              {test.metrics.totalRequests?.toLocaleString() || '0'}
                            </span>
                          </div>
                          <div>
                            <span className="text-slate-600 dark:text-slate-400">Avg Response: </span>
                            <span className="text-slate-900 dark:text-slate-100">
                              {test.metrics.averageResponseTime || 0}ms
                            </span>
                          </div>
                          <div>
                            <span className="text-slate-600 dark:text-slate-400">Error Rate: </span>
                            <span
                              className={
                                (test.metrics.errorRate || 0) > 5
                                  ? 'text-red-600 dark:text-red-400'
                                  : 'text-emerald-600 dark:text-emerald-400'
                              }
                            >
                              {(test.metrics.errorRate || 0).toFixed(2)}%
                            </span>
                          </div>
                        </div>
                      )}
                    </div>
                    <div className="flex items-center gap-2">
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={(e: React.MouseEvent<HTMLButtonElement>) => deleteTest(test.id, e)}
                      >
                        <Trash2 className="size-4 text-red-500" />
                      </Button>
                      <ChevronRight className="size-5 text-slate-400 dark:text-slate-600" />
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
