import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Play, Square, Activity, CheckCircle2, XCircle, Clock } from 'lucide-react';
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
  
  // Chart colors from CSS variables
  const chartColors = {
    text: isDark ? 'var(--text-secondary)' : 'var(--text-secondary)',
    grid: isDark ? 'var(--border)' : 'var(--border)',
    responseTime: 'var(--chart-1)',
    users: 'var(--chart-5)',
    rps: 'var(--chart-2)',
    cardBg: 'var(--card)',
    cardBorder: 'var(--card-border)',
  };
  
  return (
    <div className="space-y-6">
      <Card 
        className="shadow-sm transition-all duration-300" 
        style={{ 
          backgroundColor: 'var(--card)', 
          borderColor: 'var(--card-border)',
          boxShadow: 'var(--shadow-sm)'
        }}
      >
        <CardContent className="pt-6">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              {isRunning ? (
                <>
                  <div className="relative">
                    <div 
                      className="size-3 rounded-full animate-pulse" 
                      style={{ backgroundColor: 'var(--success)' }}
                    />
                    <div 
                      className="absolute inset-0 rounded-full animate-ping opacity-75" 
                      style={{ backgroundColor: 'var(--success)' }}
                    />
                  </div>
                  <div>
                    <p style={{ color: 'var(--success)' }}>Running</p>
                    <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Test in progress...</p>
                  </div>
                </>
              ) : (
                <>
                  <div 
                    className="size-3 rounded-full" 
                    style={{ backgroundColor: 'var(--text-muted)' }}
                  />
                  <div>
                    <p style={{ color: 'var(--text)' }}>Ready</p>
                    <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Configure and start test</p>
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

          <div 
            className="grid grid-cols-2 gap-3 p-4 rounded-lg border transition-all duration-300"
            style={{ 
              backgroundColor: 'var(--bg-secondary)', 
              borderColor: 'var(--border)' 
            }}
          >
            <div>
              <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>Test Type</p>
              <p className="capitalize" style={{ color: 'var(--text)' }}>{config.testType}</p>
            </div>
            <div>
              <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>Virtual Users</p>
              <p style={{ color: 'var(--text)' }}>{config.virtualUsers}</p>
            </div>
            <div>
              <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>Duration</p>
              <p style={{ color: 'var(--text)' }}>{config.duration}s</p>
            </div>
            <div>
              <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>Steps</p>
              <p style={{ color: 'var(--text)' }}>{steps.length || 'Single endpoint'}</p>
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
                color: 'var(--chart-1)',
                bg: isDark ? 'rgba(59, 130, 246, 0.15)' : 'rgba(59, 130, 246, 0.1)',
              },
              {
                label: 'Successful',
                value: metrics.successfulRequests.toLocaleString(),
                icon: CheckCircle2,
                color: 'var(--chart-2)',
                bg: isDark ? 'rgba(16, 185, 129, 0.15)' : 'rgba(16, 185, 129, 0.1)',
              },
              {
                label: 'Failed',
                value: metrics.failedRequests.toLocaleString(),
                icon: XCircle,
                color: 'var(--chart-4)',
                bg: isDark ? 'rgba(239, 68, 68, 0.15)' : 'rgba(239, 68, 68, 0.1)',
              },
              {
                label: 'Avg Response',
                value: `${metrics.averageResponseTime}ms`,
                icon: Clock,
                color: 'var(--chart-3)',
                bg: isDark ? 'rgba(245, 158, 11, 0.15)' : 'rgba(245, 158, 11, 0.1)',
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
                    <div className="flex items-start justify-between">
                      <div>
                        <p className="text-sm mb-1" style={{ color: 'var(--text-secondary)' }}>{metric.label}</p>
                        <p style={{ color: 'var(--text)' }}>{metric.value}</p>
                      </div>
                      <div 
                        className="p-2 rounded-lg"
                        style={{ backgroundColor: metric.bg }}
                      >
                        <Icon className="size-5" style={{ color: metric.color }} />
                      </div>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>

          <Card 
            className="shadow-sm transition-all duration-300"
            style={{ 
              backgroundColor: 'var(--card)', 
              borderColor: 'var(--card-border)',
              boxShadow: 'var(--shadow-sm)'
            }}
          >
            <CardHeader>
              <CardTitle style={{ color: 'var(--text)' }}>Response Time</CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Real-time performance metrics
                {isRunning && <span className="ml-2" style={{ color: 'var(--success)' }}>(Live)</span>}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-64">
                {responseTimeData.length > 0 ? (
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={responseTimeData}>
                      <CartesianGrid strokeDasharray="3 3" stroke={chartColors.grid} />
                      <XAxis
                        dataKey="time"
                        stroke={chartColors.text}
                        tick={{ fontSize: 12, fill: chartColors.text }}
                        label={{ 
                          value: 'Time (s)', 
                          position: 'insideBottom', 
                          offset: -5, 
                          fill: chartColors.text 
                        }}
                      />
                      <YAxis
                        stroke={chartColors.text}
                        tick={{ fontSize: 12, fill: chartColors.text }}
                        label={{ 
                          value: 'Response Time (ms)', 
                          angle: -90, 
                          position: 'insideLeft', 
                          fill: chartColors.text 
                        }}
                      />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: 'var(--card)',
                          border: `1px solid var(--border)`,
                          borderRadius: '8px',
                          color: 'var(--text)',
                        }}
                      />
                      <Line
                        type="monotone"
                        dataKey="responseTime"
                        stroke={chartColors.responseTime}
                        strokeWidth={2}
                        dot={false}
                        name="Response Time (ms)"
                      />
                    </LineChart>
                  </ResponsiveContainer>
                ) : (
                  <div 
                    className="flex items-center justify-center h-full"
                    style={{ color: 'var(--text-muted)' }}
                  >
                    Waiting for test data...
                  </div>
                )}
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
              <CardTitle style={{ color: 'var(--text)' }}>Load Pattern</CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Virtual users and throughput over time
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-64">
                {activeUsersData.length > 0 ? (
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={activeUsersData}>
                      <CartesianGrid strokeDasharray="3 3" stroke={chartColors.grid} />
                      <XAxis
                        dataKey="time"
                        stroke={chartColors.text}
                        tick={{ fontSize: 12, fill: chartColors.text }}
                        label={{ 
                          value: 'Time (s)', 
                          position: 'insideBottom', 
                          offset: -5, 
                          fill: chartColors.text 
                        }}
                      />
                      <YAxis
                        yAxisId="left"
                        stroke={chartColors.text}
                        tick={{ fontSize: 12, fill: chartColors.text }}
                        label={{
                          value: 'Virtual Users',
                          angle: -90,
                          position: 'insideLeft',
                          fill: chartColors.text,
                        }}
                      />
                      <YAxis 
                        yAxisId="right" 
                        orientation="right" 
                        stroke={chartColors.text} 
                        tick={{ fontSize: 12, fill: chartColors.text }}
                        label={{
                          value: 'Requests/s',
                          angle: 90,
                          position: 'insideRight',
                          fill: chartColors.text,
                        }}
                      />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: 'var(--card)',
                          border: `1px solid var(--border)`,
                          borderRadius: '8px',
                          color: 'var(--text)',
                        }}
                      />
                      <Legend />
                      <Line
                        yAxisId="left"
                        type="monotone"
                        dataKey="users"
                        stroke={chartColors.users}
                        strokeWidth={2}
                        dot={false}
                        name="Virtual Users"
                      />
                      <Line
                        yAxisId="right"
                        type="monotone"
                        dataKey="rps"
                        stroke={chartColors.rps}
                        strokeWidth={2}
                        dot={false}
                        name="Requests/s"
                      />
                    </LineChart>
                  </ResponsiveContainer>
                ) : (
                  <div 
                    className="flex items-center justify-center h-full"
                    style={{ color: 'var(--text-muted)' }}
                  >
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
