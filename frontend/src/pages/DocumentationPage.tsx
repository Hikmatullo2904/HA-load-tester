import { Navigation } from '../components/Navigation';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { BookOpen, Zap, Users, Clock, Code, BarChart3, Shield } from 'lucide-react';

export function DocumentationPage() {
  const sections = [
    {
      icon: Zap,
      title: 'Getting Started',
      content: (
        <div className="space-y-4" style={{ color: 'var(--text-secondary)' }}>
          <p>
            Load Tester is a modern, beginner-friendly API load testing platform. It's simpler than
            JMeter and more intuitive than Locust, but with all the power you need.
          </p>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Quick Start</h4>
            <ol className="list-decimal list-inside space-y-2 ml-2">
              <li>Navigate to "Create Test" from the menu</li>
              <li>Enter your API endpoint URL</li>
              <li>Configure test parameters (users, duration, test type)</li>
              <li>Click "Start Test" to begin</li>
              <li>View real-time metrics and results</li>
            </ol>
          </div>
        </div>
      ),
    },
    {
      icon: Users,
      title: 'Test Types',
      content: (
        <div className="space-y-4" style={{ color: 'var(--text-secondary)' }}>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Fixed Load</h4>
            <p>
              Maintains a constant number of virtual users throughout the test. Best for steady-state
              performance testing.
            </p>
          </div>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Ramp-up Load</h4>
            <p>
              Gradually increases the number of users over time. Useful for finding the breaking point
              of your API.
            </p>
          </div>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Spike Test</h4>
            <p>
              Simulates sudden traffic spikes. Helps identify how your API handles unexpected load
              increases.
            </p>
          </div>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Stress Test</h4>
            <p>
              Progressively increases load in steps. Ideal for determining maximum capacity and
              failure points.
            </p>
          </div>
        </div>
      ),
    },
    {
      icon: Code,
      title: 'Scenario Builder',
      content: (
        <div className="space-y-4" style={{ color: 'var(--text-secondary)' }}>
          <p>
            Create multi-step test scenarios to simulate complex user workflows. Each step can have its
            own method, URL, headers, and body.
          </p>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Step Features</h4>
            <ul className="list-disc list-inside space-y-1 ml-2">
              <li>Drag & drop to reorder steps</li>
              <li>Configure HTTP method (GET, POST, PUT, DELETE, PATCH)</li>
              <li>Set request weight for distribution</li>
              <li>Add delay between steps</li>
              <li>Define expected status codes</li>
              <li>Configure retry logic</li>
              <li>Custom headers and request bodies</li>
            </ul>
          </div>
        </div>
      ),
    },
    {
      icon: Shield,
      title: 'Authentication',
      content: (
        <div className="space-y-4" style={{ color: 'var(--text-secondary)' }}>
          <p>Load Tester supports multiple authentication methods:</p>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Bearer Token</h4>
            <p>Add your token in the Authentication section. It will be sent as:</p>
            <code 
              className="block mt-2 p-2 rounded text-sm"
              style={{ 
                backgroundColor: 'var(--bg-secondary)', 
                color: 'var(--text)'
              }}
            >
              Authorization: Bearer YOUR_TOKEN
            </code>
          </div>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Basic Auth</h4>
            <p>Provide username and password. They will be base64 encoded automatically.</p>
          </div>
          <p className="text-sm" style={{ color: 'var(--text-muted)' }}>
            You can save authentication presets in Settings for easy reuse.
          </p>
        </div>
      ),
    },
    {
      icon: BarChart3,
      title: 'Metrics & Results',
      content: (
        <div className="space-y-4" style={{ color: 'var(--text-secondary)' }}>
          <p>Load Tester provides comprehensive metrics to analyze your API performance:</p>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Key Metrics</h4>
            <ul className="list-disc list-inside space-y-1 ml-2">
              <li>Total Requests - Number of requests sent</li>
              <li>Success/Failure Rate - Percentage of successful vs failed requests</li>
              <li>Response Time - Average, min, max, and percentiles (P50, P95, P99)</li>
              <li>Requests Per Second (RPS) - Throughput over time</li>
              <li>Active Virtual Users - Number of concurrent users</li>
              <li>Error Rate - Percentage of failed requests</li>
            </ul>
          </div>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Charts</h4>
            <ul className="list-disc list-inside space-y-1 ml-2">
              <li>Response Time Over Time - Performance trends</li>
              <li>RPS Chart - Throughput visualization</li>
              <li>Percentile Distribution - Response time distribution</li>
              <li>Error Timeline - Error occurrences over time</li>
              <li>Load Pattern - Virtual users and RPS curves</li>
            </ul>
          </div>
        </div>
      ),
    },
    {
      icon: Clock,
      title: 'Best Practices',
      content: (
        <div className="space-y-4" style={{ color: 'var(--text-secondary)' }}>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Test Configuration</h4>
            <ul className="list-disc list-inside space-y-1 ml-2">
              <li>Start with small user counts and gradually increase</li>
              <li>Use appropriate test durations (30-60 seconds for quick tests)</li>
              <li>Monitor your server resources during testing</li>
              <li>Test in staging environments before production</li>
            </ul>
          </div>
          <div>
            <h4 className="font-medium mb-2" style={{ color: 'var(--text)' }}>Interpreting Results</h4>
            <ul className="list-disc list-inside space-y-1 ml-2">
              <li>P95 and P99 percentiles show worst-case performance</li>
              <li>Error rates above 1% may indicate issues</li>
              <li>Compare results across multiple test runs</li>
              <li>Look for response time degradation under load</li>
            </ul>
          </div>
        </div>
      ),
    },
  ];

  return (
    <div className="bg-slate-50 min-h-screen dark:bg-slate-950 transition-all duration-300" style={{ backgroundColor: 'var(--bg)' }}>
      <Navigation />

      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-4">
            <div className="size-12 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
              <BookOpen className="size-6 text-white" />
            </div>
            <div>
              <h1 style={{ color: 'var(--text)' }}>Documentation</h1>
              <p style={{ color: 'var(--text-secondary)' }}>
                Learn how to use Load Tester effectively
              </p>
            </div>
          </div>
        </div>

        <div className="space-y-6">
          {sections.map((section) => {
            const Icon = section.icon;
            return (
              <Card
                key={section.title}
                className="shadow-sm transition-all duration-300"
                style={{ 
                  backgroundColor: 'var(--card)', 
                  borderColor: 'var(--card-border)',
                  boxShadow: 'var(--shadow-sm)'
                }}
              >
                <CardHeader>
                  <CardTitle className="flex items-center gap-2" style={{ color: 'var(--text)' }}>
                    <Icon className="size-5" style={{ color: 'var(--chart-5)' }} />
                    {section.title}
                  </CardTitle>
                </CardHeader>
                <CardContent>{section.content}</CardContent>
              </Card>
            );
          })}
        </div>
      </div>
    </div>
  );
}

