import { TestMetrics } from '../App';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { Activity, CheckCircle2, XCircle, Clock, Zap, TrendingUp, TrendingDown } from 'lucide-react';

interface TestResultsProps {
  metrics: TestMetrics;
  responseTimeData: Array<{ time: number; responseTime: number }>;
  activeUsersData: Array<{ time: number; users: number; rps: number }>;
  isRunning: boolean;
}

export function TestResults({ metrics, responseTimeData, activeUsersData, isRunning }: TestResultsProps) {
  const metricCards = [
    {
      title: 'Total Requests',
      value: metrics.totalRequests.toLocaleString(),
      icon: Activity,
      color: 'text-blue-600',
      bgColor: 'bg-blue-50',
    },
    {
      title: 'Successful',
      value: metrics.successfulRequests.toLocaleString(),
      icon: CheckCircle2,
      color: 'text-emerald-600',
      bgColor: 'bg-emerald-50',
    },
    {
      title: 'Failed',
      value: metrics.failedRequests.toLocaleString(),
      icon: XCircle,
      color: 'text-red-600',
      bgColor: 'bg-red-50',
    },
    {
      title: 'Avg Response Time',
      value: `${metrics.averageResponseTime}ms`,
      icon: Clock,
      color: 'text-amber-600',
      bgColor: 'bg-amber-50',
    },
    {
      title: 'Requests/Second',
      value: metrics.requestsPerSecond.toFixed(2),
      icon: Zap,
      color: 'text-purple-600',
      bgColor: 'bg-purple-50',
    },
    {
      title: 'Error Rate',
      value: `${metrics.errorRate.toFixed(2)}%`,
      icon: metrics.errorRate > 5 ? TrendingUp : TrendingDown,
      color: metrics.errorRate > 5 ? 'text-red-600' : 'text-emerald-600',
      bgColor: metrics.errorRate > 5 ? 'bg-red-50' : 'bg-emerald-50',
    },
  ];

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {metricCards.map((metric) => {
          const Icon = metric.icon;
          return (
            <Card key={metric.title}>
              <CardContent className="pt-6">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-slate-600 text-sm mb-1">{metric.title}</p>
                    <p className="text-slate-900">{metric.value}</p>
                  </div>
                  <div className={`p-2 rounded-lg ${metric.bgColor}`}>
                    <Icon className={`size-5 ${metric.color}`} />
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Response Time</CardTitle>
          <CardDescription>
            Real-time response time visualization
            {isRunning && <span className="ml-2 text-emerald-600">(Live)</span>}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="h-64">
            {responseTimeData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={responseTimeData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                  <XAxis
                    dataKey="time"
                    stroke="#64748b"
                    tick={{ fontSize: 12 }}
                    label={{ value: 'Time (s)', position: 'insideBottom', offset: -5 }}
                  />
                  <YAxis
                    stroke="#64748b"
                    tick={{ fontSize: 12 }}
                    label={{ value: 'Response Time (ms)', angle: -90, position: 'insideLeft' }}
                  />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#fff',
                      border: '1px solid #e2e8f0',
                      borderRadius: '8px',
                    }}
                    labelFormatter={(value) => `Time: ${value}s`}
                    formatter={(value: number) => [`${value}ms`, 'Response Time']}
                  />
                  <Line
                    type="monotone"
                    dataKey="responseTime"
                    stroke="#3b82f6"
                    strokeWidth={2}
                    dot={false}
                    animationDuration={300}
                  />
                </LineChart>
              </ResponsiveContainer>
            ) : (
              <div className="flex items-center justify-center h-full text-slate-400">
                No data yet. Start a test to see results.
              </div>
            )}
          </div>

          <div className="mt-6 grid grid-cols-3 gap-4 pt-4 border-t border-slate-200">
            <div>
              <p className="text-slate-600 text-sm">Min Response Time</p>
              <p className="text-slate-900">{metrics.minResponseTime}ms</p>
            </div>
            <div>
              <p className="text-slate-600 text-sm">Avg Response Time</p>
              <p className="text-slate-900">{metrics.averageResponseTime}ms</p>
            </div>
            <div>
              <p className="text-slate-600 text-sm">Max Response Time</p>
              <p className="text-slate-900">{metrics.maxResponseTime}ms</p>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Active Load Pattern</CardTitle>
          <CardDescription>
            Virtual users and requests per second over time
            {isRunning && <span className="ml-2 text-emerald-600">(Live)</span>}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="h-64">
            {activeUsersData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={activeUsersData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                  <XAxis
                    dataKey="time"
                    stroke="#64748b"
                    tick={{ fontSize: 12 }}
                    label={{ value: 'Time (s)', position: 'insideBottom', offset: -5 }}
                  />
                  <YAxis
                    yAxisId="left"
                    stroke="#64748b"
                    tick={{ fontSize: 12 }}
                    label={{ value: 'Virtual Users', angle: -90, position: 'insideLeft' }}
                  />
                  <YAxis
                    yAxisId="right"
                    orientation="right"
                    stroke="#64748b"
                    tick={{ fontSize: 12 }}
                    label={{ value: 'Requests/s', angle: 90, position: 'insideRight' }}
                  />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#fff',
                      border: '1px solid #e2e8f0',
                      borderRadius: '8px',
                    }}
                    labelFormatter={(value) => `Time: ${value}s`}
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
                    animationDuration={300}
                  />
                  <Line
                    yAxisId="right"
                    type="monotone"
                    dataKey="rps"
                    stroke="#10b981"
                    strokeWidth={2}
                    dot={false}
                    name="Requests/s"
                    animationDuration={300}
                  />
                </LineChart>
              </ResponsiveContainer>
            ) : (
              <div className="flex items-center justify-center h-full text-slate-400">
                No data yet. Start a test to see load pattern.
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}