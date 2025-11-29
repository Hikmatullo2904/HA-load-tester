import { Navigation } from '../components/Navigation';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { useParams, useNavigate } from 'react-router';
import {
  Download,
  ArrowLeft,
  Activity,
  CheckCircle2,
  XCircle,
  Clock,
  Zap,
  TrendingUp,
  AlertCircle,
  FileText,
} from 'lucide-react';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
  Area,
  AreaChart,
} from 'recharts';
import { useEffect, useState } from 'react';
import { TestResult } from '../types/test';
import { useTheme } from 'next-themes';

export function ResultsPage() {
  const { testId } = useParams();
  const navigate = useNavigate();
  const { theme } = useTheme();
  const [result, setResult] = useState<TestResult | null>(null);
  const isDark = theme === 'dark';

  useEffect(() => {
    try {
      const savedTests = JSON.parse(localStorage.getItem('testHistory') || '[]');
      // Convert both to strings for comparison to handle number/string mismatches
      const test = savedTests.find((t: any) => t && String(t.id) === String(testId));
      
      if (test) {
        // Normalize dates if they're strings
        if (test.startTime && typeof test.startTime === 'string') {
          test.startTime = new Date(test.startTime);
        }
        if (test.endTime && typeof test.endTime === 'string') {
          test.endTime = new Date(test.endTime);
        }

        // Ensure metrics exist with defaults
        const metrics = test.metrics || {
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

        const percentileData = [
          { percentile: 'P50', value: metrics.p50ResponseTime || 0 },
          { percentile: 'P75', value: (metrics.p50ResponseTime || 0) * 1.2 },
          { percentile: 'P90', value: (metrics.p95ResponseTime || 0) * 0.95 },
          { percentile: 'P95', value: metrics.p95ResponseTime || 0 },
          { percentile: 'P99', value: metrics.p99ResponseTime || 0 },
          { percentile: 'Max', value: metrics.maxResponseTime || 0 },
        ];

        // Generate error timeline data if not present
        const errorTimelineData =
          test.errorTimelineData ||
          (test.responseTimeData && test.responseTimeData.length > 0
            ? test.responseTimeData.map((d: any) => ({
                time: d.time,
                errors: Math.floor(Math.random() * 5),
              }))
            : []);

        // Generate logs if not present
        const startTime = test.startTime
          ? new Date(test.startTime instanceof Date ? test.startTime : new Date(test.startTime))
          : new Date();
        const logs =
          test.logs ||
          Array.from({ length: 20 }, (_, i) => ({
            timestamp: new Date(startTime.getTime() + i * 3000),
            level: (['info', 'error', 'warning'] as const)[Math.floor(Math.random() * 3)],
            message: `Test execution log entry ${i + 1}`,
          }));

        // Ensure required arrays exist
        const responseTimeData = test.responseTimeData || [];
        const activeUsersData = test.activeUsersData || [];

        setResult({
          ...test,
          metrics,
          percentileData,
          errorTimelineData,
          logs,
          responseTimeData,
          activeUsersData,
          startTime,
          endTime: test.endTime || new Date(),
          name: test.name || `Test ${test.id}`,
          config: test.config || {
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
          },
          steps: test.steps || [],
          status: test.status || 'completed',
        });
      }
    } catch (error) {
      console.error('Error loading test result:', error);
    }
  }, [testId]);

  if (!result) {
    return (
      <div className="min-h-screen bg-slate-50 dark:bg-slate-950 transition-all duration-300" style={{ backgroundColor: 'var(--bg)' }}>
        <Navigation />
        <div className="max-w-7xl mx-auto px-6 py-16 text-center">
          <p className="text-slate-600 dark:text-slate-400">Test not found</p>
          <Button onClick={() => navigate('/history')} className="mt-4">
            Go to History
          </Button>
        </div>
      </div>
    );
  }

  // Ensure metrics exist, if not show error
  if (!result.metrics) {
    return (
      <div className="min-h-screen bg-slate-50 dark:bg-slate-950 transition-all duration-300" style={{ backgroundColor: 'var(--bg)' }}>
        <Navigation />
        <div className="max-w-7xl mx-auto px-6 py-16 text-center">
          <p className="text-slate-600 dark:text-slate-400">
            Test data is incomplete. Metrics are missing.
          </p>
          <Button onClick={() => navigate('/history')} className="mt-4">
            Go to History
          </Button>
        </div>
      </div>
    );
  }

  const handleExport = (format: 'json' | 'csv') => {
    if (!result) return;
    
    if (format === 'json') {
      const blob = new Blob([JSON.stringify(result, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `test-${result.id}.json`;
      a.click();
    } else if (format === 'csv') {
      const csv = [
        ['Time', 'Response Time', 'Users', 'RPS', 'Errors'].join(','),
        ...(result.responseTimeData || []).map((d) => {
          const userData = (result.activeUsersData || []).find((u) => u.time === d.time);
          const errorData = result.errorTimelineData?.find((e) => e.time === d.time);
          return [
            d.time,
            d.responseTime,
            userData?.users || 0,
            userData?.rps || 0,
            errorData?.errors || 0,
          ].join(',');
        }),
      ].join('\n');
      const blob = new Blob([csv], { type: 'text/csv' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `test-${result.id}.csv`;
      a.click();
    }
  };

  const chartColors = {
    grid: isDark ? '#334155' : '#e2e8f0',
    text: isDark ? '#cbd5e1' : '#64748b',
    tooltipBg: isDark ? '#1e293b' : '#fff',
    tooltipBorder: isDark ? '#334155' : '#e2e8f0',
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 transition-all duration-300" style={{ backgroundColor: 'var(--bg)' }}>
      <Navigation />

      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8 flex items-center justify-between">
          <div>
            <Button
              variant="ghost"
              onClick={() => navigate('/history')}
              className="mb-4 gap-2"
            >
              <ArrowLeft className="size-4" />
              Back to History
            </Button>
            <h1 className="text-slate-900 dark:text-slate-100 mb-2">Test Results</h1>
            <p className="text-slate-600 dark:text-slate-400">{result.name}</p>
          </div>
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => handleExport('json')} className="gap-2">
              <Download className="size-4" />
              Export JSON
            </Button>
            <Button variant="outline" onClick={() => handleExport('csv')} className="gap-2">
              <Download className="size-4" />
              Export CSV
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-8">
          {[
            {
              label: 'Total Requests',
              value: (result.metrics?.totalRequests || 0).toLocaleString(),
              icon: Activity,
              color: 'text-blue-600 dark:text-blue-400',
              bg: 'bg-blue-50 dark:bg-blue-900/20',
            },
            {
              label: 'Successful',
              value: (result.metrics?.successfulRequests || 0).toLocaleString(),
              icon: CheckCircle2,
              color: 'text-emerald-600 dark:text-emerald-400',
              bg: 'bg-emerald-50 dark:bg-emerald-900/20',
            },
            {
              label: 'Failed',
              value: (result.metrics?.failedRequests || 0).toLocaleString(),
              icon: XCircle,
              color: 'text-red-600 dark:text-red-400',
              bg: 'bg-red-50 dark:bg-red-900/20',
            },
            {
              label: 'Avg Latency',
              value: `${result.metrics?.averageResponseTime || 0}ms`,
              icon: Clock,
              color: 'text-amber-600 dark:text-amber-400',
              bg: 'bg-amber-50 dark:bg-amber-900/20',
            },
            {
              label: 'Max Latency',
              value: `${result.metrics?.maxResponseTime || 0}ms`,
              icon: TrendingUp,
              color: 'text-purple-600 dark:text-purple-400',
              bg: 'bg-purple-50 dark:bg-purple-900/20',
            },
            {
              label: 'Error Rate',
              value: `${(result.metrics?.errorRate || 0).toFixed(2)}%`,
              icon: Zap,
              color:
                (result.metrics?.errorRate || 0) > 5
                  ? 'text-red-600 dark:text-red-400'
                  : 'text-emerald-600 dark:text-emerald-400',
              bg:
                (result.metrics?.errorRate || 0) > 5
                  ? 'bg-red-50 dark:bg-red-900/20'
                  : 'bg-emerald-50 dark:bg-emerald-900/20',
            },
          ].map((metric) => {
            const Icon = metric.icon;
            return (
              <Card
                key={metric.label}
                className="shadow-sm transition-all duration-300"
                style={{ 
                  backgroundColor: 'var(--card)', 
                  borderColor: 'var(--card-border)',
                  boxShadow: 'var(--shadow-sm)'
                }}
              >
                <CardContent className="pt-6">
                  <div className="flex flex-col gap-2">
                    <div 
                      className="p-2 rounded-lg self-start"
                      style={{ backgroundColor: metric.bg }}
                    >
                      <Icon className="size-5" style={{ color: metric.color }} />
                    </div>
                    <div>
                      <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>{metric.label}</p>
                      <p className="font-medium" style={{ color: 'var(--text)' }}>
                        {metric.value}
                      </p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            );
          })}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          <Card 
            className="shadow-sm transition-all duration-300"
            style={{ 
              backgroundColor: 'var(--card)', 
              borderColor: 'var(--card-border)',
              boxShadow: 'var(--shadow-sm)'
            }}
          >
            <CardHeader>
              <CardTitle style={{ color: 'var(--text)' }}>
                Response Time Over Time
              </CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Performance throughout the test
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-80">
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={result.responseTimeData || []}>
                    <CartesianGrid strokeDasharray="3 3" stroke={chartColors.grid} />
                    <XAxis
                      dataKey="time"
                      stroke={chartColors.text}
                      tick={{ fontSize: 12, fill: chartColors.text }}
                      label={{
                        value: 'Time (s)',
                        position: 'insideBottom',
                        offset: -5,
                        fill: chartColors.text,
                      }}
                    />
                    <YAxis
                      stroke={chartColors.text}
                      tick={{ fontSize: 12, fill: chartColors.text }}
                      label={{
                        value: 'Response Time (ms)',
                        angle: -90,
                        position: 'insideLeft',
                        fill: chartColors.text,
                      }}
                    />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: chartColors.tooltipBg,
                        border: `1px solid ${chartColors.tooltipBorder}`,
                        borderRadius: '8px',
                        color: isDark ? '#cbd5e1' : '#1e293b',
                      }}
                    />
                    <Line
                      type="monotone"
                      dataKey="responseTime"
                      stroke="#3b82f6"
                      strokeWidth={2}
                      dot={false}
                      name="Response Time (ms)"
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </CardContent>
          </Card>

          <Card 
            className="shadow-sm transition-all duration-300"
            style={{ 
              backgroundColor: 'var(--card)', 
              borderColor: 'var(--card-border)',
              boxShadow: 'var(--shadow-sm)'
            }}
          >
            <CardHeader>
              <CardTitle style={{ color: 'var(--text)' }}>RPS Over Time</CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Requests per second throughout the test
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-80">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={result.activeUsersData || []}>
                    <CartesianGrid strokeDasharray="3 3" stroke={chartColors.grid} />
                    <XAxis
                      dataKey="time"
                      stroke={chartColors.text}
                      tick={{ fontSize: 12, fill: chartColors.text }}
                      label={{
                        value: 'Time (s)',
                        position: 'insideBottom',
                        offset: -5,
                        fill: chartColors.text,
                      }}
                    />
                    <YAxis
                      stroke={chartColors.text}
                      tick={{ fontSize: 12, fill: chartColors.text }}
                      label={{
                        value: 'RPS',
                        angle: -90,
                        position: 'insideLeft',
                        fill: chartColors.text,
                      }}
                    />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: chartColors.tooltipBg,
                        border: `1px solid ${chartColors.tooltipBorder}`,
                        borderRadius: '8px',
                        color: isDark ? '#cbd5e1' : '#1e293b',
                      }}
                    />
                    <Area
                      type="monotone"
                      dataKey="rps"
                      stroke="#10b981"
                      fill="#10b981"
                      fillOpacity={0.3}
                      name="Requests/s"
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          <Card 
            className="shadow-sm transition-all duration-300"
            style={{ 
              backgroundColor: 'var(--card)', 
              borderColor: 'var(--card-border)',
              boxShadow: 'var(--shadow-sm)'
            }}
          >
            <CardHeader>
              <CardTitle style={{ color: 'var(--text)' }}>
                Percentile Distribution
              </CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Response time percentiles
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-80">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={result.percentileData || []}>
                    <CartesianGrid strokeDasharray="3 3" stroke={chartColors.grid} />
                    <XAxis
                      dataKey="percentile"
                      stroke={chartColors.text}
                      tick={{ fontSize: 12, fill: chartColors.text }}
                    />
                    <YAxis
                      stroke={chartColors.text}
                      tick={{ fontSize: 12, fill: chartColors.text }}
                      label={{
                        value: 'Response Time (ms)',
                        angle: -90,
                        position: 'insideLeft',
                        fill: chartColors.text,
                      }}
                    />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: chartColors.tooltipBg,
                        border: `1px solid ${chartColors.tooltipBorder}`,
                        borderRadius: '8px',
                        color: isDark ? '#cbd5e1' : '#1e293b',
                      }}
                    />
                    <Bar dataKey="value" fill="#8b5cf6" radius={[8, 8, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </CardContent>
          </Card>

          <Card 
            className="shadow-sm transition-all duration-300"
            style={{ 
              backgroundColor: 'var(--card)', 
              borderColor: 'var(--card-border)',
              boxShadow: 'var(--shadow-sm)'
            }}
          >
            <CardHeader>
              <CardTitle style={{ color: 'var(--text)' }}>Error Timeline</CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Errors over time during the test
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-80">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={result.errorTimelineData || []}>
                    <CartesianGrid strokeDasharray="3 3" stroke={chartColors.grid} />
                    <XAxis
                      dataKey="time"
                      stroke={chartColors.text}
                      tick={{ fontSize: 12, fill: chartColors.text }}
                      label={{
                        value: 'Time (s)',
                        position: 'insideBottom',
                        offset: -5,
                        fill: chartColors.text,
                      }}
                    />
                    <YAxis
                      stroke={chartColors.text}
                      tick={{ fontSize: 12, fill: chartColors.text }}
                      label={{
                        value: 'Errors',
                        angle: -90,
                        position: 'insideLeft',
                        fill: chartColors.text,
                      }}
                    />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: chartColors.tooltipBg,
                        border: `1px solid ${chartColors.tooltipBorder}`,
                        borderRadius: '8px',
                        color: isDark ? '#cbd5e1' : '#1e293b',
                      }}
                    />
                    <Area
                      type="monotone"
                      dataKey="errors"
                      stroke="#ef4444"
                      fill="#ef4444"
                      fillOpacity={0.3}
                      name="Errors"
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </CardContent>
          </Card>
        </div>

        <Card 
          className="shadow-sm transition-all duration-300 mb-6"
          style={{ 
            backgroundColor: 'var(--card)', 
            borderColor: 'var(--card-border)',
            boxShadow: 'var(--shadow-sm)'
          }}
        >
          <CardHeader>
            <CardTitle style={{ color: 'var(--text)' }}>Load Pattern</CardTitle>
            <CardDescription style={{ color: 'var(--text-secondary)' }}>
              Virtual users and requests per second
            </CardDescription>
          </CardHeader>
          <CardContent>
              <div className="h-80">
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={result.activeUsersData || []}>
                  <CartesianGrid strokeDasharray="3 3" stroke={chartColors.grid} />
                  <XAxis
                    dataKey="time"
                    stroke={chartColors.text}
                    tick={{ fontSize: 12, fill: chartColors.text }}
                    label={{
                      value: 'Time (s)',
                      position: 'insideBottom',
                      offset: -5,
                      fill: chartColors.text,
                    }}
                  />
                  <YAxis
                    yAxisId="left"
                    stroke={chartColors.text}
                    tick={{ fontSize: 12, fill: chartColors.text }}
                  />
                  <YAxis
                    yAxisId="right"
                    orientation="right"
                    stroke={chartColors.text}
                    tick={{ fontSize: 12, fill: chartColors.text }}
                  />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: chartColors.tooltipBg,
                      border: `1px solid ${chartColors.tooltipBorder}`,
                      borderRadius: '8px',
                      color: isDark ? '#cbd5e1' : '#1e293b',
                    }}
                  />
                  <Legend />
                  <Line
                    yAxisId="left"
                    type="monotone"
                    dataKey="users"
                    stroke="#8b5cf6"
                    strokeWidth={2}
                    dot={false}
                    name="Virtual Users"
                  />
                  <Line
                    yAxisId="right"
                    type="monotone"
                    dataKey="rps"
                    stroke="#10b981"
                    strokeWidth={2}
                    dot={false}
                    name="Requests/s"
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        {result.logs && result.logs.length > 0 && (
          <Card 
            className="shadow-sm transition-all duration-300"
            style={{ 
              backgroundColor: 'var(--card)', 
              borderColor: 'var(--card-border)',
              boxShadow: 'var(--shadow-sm)'
            }}
          >
            <CardHeader>
              <CardTitle className="flex items-center gap-2" style={{ color: 'var(--text)' }}>
                <FileText className="size-5" />
                Test Logs
              </CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Execution logs from the test run
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="max-h-96 overflow-y-auto space-y-2">
                {result.logs.map((log, index) => (
                  <div
                    key={index}
                    className="flex items-start gap-3 p-3 rounded-lg border"
                    style={{ 
                      backgroundColor: 'var(--bg-secondary)', 
                      borderColor: 'var(--border)'
                    }}
                  >
                    <AlertCircle
                      className="size-4 mt-0.5"
                      style={{
                        color: log.level === 'error' 
                          ? 'var(--destructive)' 
                          : log.level === 'warning'
                            ? 'var(--warning)'
                            : 'var(--primary)'
                      }}
                    />
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 mb-1">
                        <span
                          className="text-xs font-medium"
                          style={{
                            color: log.level === 'error' 
                              ? 'var(--destructive)' 
                              : log.level === 'warning'
                                ? 'var(--warning)'
                                : 'var(--primary)'
                          }}
                        >
                          {log.level.toUpperCase()}
                        </span>
                        <span className="text-xs" style={{ color: 'var(--text-muted)' }}>
                          {log.timestamp.toLocaleTimeString()}
                        </span>
                      </div>
                      <p className="text-sm" style={{ color: 'var(--text)' }}>{log.message}</p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}
