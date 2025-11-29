import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Play, Square, Activity, CheckCircle2, XCircle, Clock, Zap } from 'lucide-react';
import { TestConfig, TestStep, TestMetrics } from '../types/test';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { useTheme } from 'next-themes';

interface TestSummaryPanelProps {
  config: TestConfig;
  steps: TestStep[];
  isRunning: boolean;
  metrics: TestMetrics | null;
  responseTimeData: Array<{ time: number; responseTime: number }>;
  activeUsersData: Array<{ time: number; users: number; rps: number }>;
  onStart: () => void;
  onStop: () => void;
}

export function TestSummaryPanel({
  config,
  steps,
  isRunning,
  metrics,
  responseTimeData,
  activeUsersData,
  onStart,
  onStop,
}: TestSummaryPanelProps) {
  const { theme } = useTheme();
  const isDark = theme === 'dark';
  
  return (
    <div className="space-y-6">
      <Card className="shadow-sm border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
        <CardContent className="pt-6">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              {isRunning ? (
                <>
                  <div className="relative">
                    <div className="size-3 bg-emerald-500 rounded-full animate-pulse" />
                    <div className="absolute inset-0 bg-emerald-500 rounded-full animate-ping opacity-75" />
                  </div>
                  <div>
                    <p className="text-emerald-600 dark:text-emerald-400">Running</p>
                    <p className="text-slate-500 dark:text-slate-400 text-sm">Test in progress...</p>
                  </div>
                </>
              ) : (
                <>
                  <div className="size-3 bg-slate-400 dark:bg-slate-600 rounded-full" />
                  <div>
                    <p className="text-slate-700 dark:text-slate-300">Ready</p>
                    <p className="text-slate-500 dark:text-slate-400 text-sm">Configure and start test</p>
                  </div>
                </>
              )}
            </div>

            {!isRunning ? (
              <Button onClick={onStart} size="lg" className="gap-2 shadow-sm">
                <Play className="size-5" />
                Start Test
              </Button>
            ) : (
              <Button onClick={onStop} variant="destructive" size="lg" className="gap-2 shadow-sm">
                <Square className="size-4" />
                Stop Test
              </Button>
            )}
          </div>

          <div className="grid grid-cols-2 gap-3 p-4 bg-slate-50 dark:bg-slate-800/50 rounded-lg border border-slate-200 dark:border-slate-700">
            <div>
              <p className="text-slate-600 dark:text-slate-400 text-sm">Test Type</p>
              <p className="text-slate-900 dark:text-slate-100 capitalize">{config.testType}</p>
            </div>
            <div>
              <p className="text-slate-600 dark:text-slate-400 text-sm">Virtual Users</p>
              <p className="text-slate-900 dark:text-slate-100">{config.virtualUsers}</p>
            </div>
            <div>
              <p className="text-slate-600 dark:text-slate-400 text-sm">Duration</p>
              <p className="text-slate-900 dark:text-slate-100">{config.duration}s</p>
            </div>
            <div>
              <p className="text-slate-600 dark:text-slate-400 text-sm">Steps</p>
              <p className="text-slate-900 dark:text-slate-100">{steps.length || 'Single endpoint'}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {metrics && (
        <>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[
              {
                label: 'Total Requests',
                value: metrics.totalRequests.toLocaleString(),
                icon: Activity,
                color: 'text-blue-600',
                bg: 'bg-blue-50',
              },
              {
                label: 'Successful',
                value: metrics.successfulRequests.toLocaleString(),
                icon: CheckCircle2,
                color: 'text-emerald-600',
                bg: 'bg-emerald-50',
              },
              {
                label: 'Failed',
                value: metrics.failedRequests.toLocaleString(),
                icon: XCircle,
                color: 'text-red-600',
                bg: 'bg-red-50',
              },
              {
                label: 'Avg Response',
                value: `${metrics.averageResponseTime}ms`,
                icon: Clock,
                color: 'text-amber-600',
                bg: 'bg-amber-50',
              },
            ].map((metric) => {
              const Icon = metric.icon;
              return (
                <Card key={metric.label} className="shadow-sm border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
                  <CardContent className="pt-6">
                    <div className="flex items-start justify-between">
                      <div>
                        <p className="text-slate-600 dark:text-slate-400 text-sm mb-1">{metric.label}</p>
                        <p className="text-slate-900 dark:text-slate-100">{metric.value}</p>
                      </div>
                      <div className={`p-2 rounded-lg ${metric.bg}`}>
                        <Icon className={`size-5 ${metric.color}`} />
                      </div>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>

          <Card className="shadow-sm border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
            <CardHeader>
              <CardTitle className="text-slate-900 dark:text-slate-100">Response Time</CardTitle>
              <CardDescription className="text-slate-600 dark:text-slate-400">
                Real-time performance metrics
                {isRunning && <span className="ml-2 text-emerald-600 dark:text-emerald-400">(Live)</span>}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-64">
                {responseTimeData.length > 0 ? (
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={responseTimeData}>
                      <CartesianGrid strokeDasharray="3 3" stroke={isDark ? '#334155' : '#e2e8f0'} />
                      <XAxis
                        dataKey="time"
                        stroke={isDark ? '#cbd5e1' : '#64748b'}
                        tick={{ fontSize: 12, fill: isDark ? '#cbd5e1' : '#64748b' }}
                        label={{ value: 'Time (s)', position: 'insideBottom', offset: -5, fill: isDark ? '#cbd5e1' : '#64748b' }}
                      />
                      <YAxis
                        stroke={isDark ? '#cbd5e1' : '#64748b'}
                        tick={{ fontSize: 12, fill: isDark ? '#cbd5e1' : '#64748b' }}
                        label={{ value: 'Response Time (ms)', angle: -90, position: 'insideLeft', fill: isDark ? '#cbd5e1' : '#64748b' }}
                      />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: isDark ? '#1e293b' : '#fff',
                          border: `1px solid ${isDark ? '#334155' : '#e2e8f0'}`,
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
                ) : (
                  <div className="flex items-center justify-center h-full text-slate-400 dark:text-slate-600">
                    Waiting for test data...
                  </div>
                )}
              </div>
            </CardContent>
          </Card>

          <Card className="shadow-sm border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
            <CardHeader>
              <CardTitle className="text-slate-900 dark:text-slate-100">Load Pattern</CardTitle>
              <CardDescription className="text-slate-600 dark:text-slate-400">Virtual users and throughput over time</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-64">
                {activeUsersData.length > 0 ? (
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={activeUsersData}>
                      <CartesianGrid strokeDasharray="3 3" stroke={isDark ? '#334155' : '#e2e8f0'} />
                      <XAxis
                        dataKey="time"
                        stroke={isDark ? '#cbd5e1' : '#64748b'}
                        tick={{ fontSize: 12, fill: isDark ? '#cbd5e1' : '#64748b' }}
                        label={{ value: 'Time (s)', position: 'insideBottom', offset: -5, fill: isDark ? '#cbd5e1' : '#64748b' }}
                      />
                      <YAxis
                        yAxisId="left"
                        stroke={isDark ? '#cbd5e1' : '#64748b'}
                        tick={{ fontSize: 12, fill: isDark ? '#cbd5e1' : '#64748b' }}
                      />
                      <YAxis yAxisId="right" orientation="right" stroke={isDark ? '#cbd5e1' : '#64748b'} tick={{ fontSize: 12, fill: isDark ? '#cbd5e1' : '#64748b' }} />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: isDark ? '#1e293b' : '#fff',
                          border: `1px solid ${isDark ? '#334155' : '#e2e8f0'}`,
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
                ) : (
                  <div className="flex items-center justify-center h-full text-slate-400 dark:text-slate-600">
                    Waiting for test data...
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        </>
      )}
    </div>
  );
}
