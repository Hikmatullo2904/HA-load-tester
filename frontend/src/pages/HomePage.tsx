import { Navigation } from '../components/Navigation';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Link } from 'react-router';
import { Plus, Zap, BarChart3, Clock, Shield, Code } from 'lucide-react';

export function HomePage() {
  const features = [
    {
      icon: Zap,
      title: 'Multiple Test Types',
      description: 'Fixed load, ramp-up, spike, and stress testing modes',
    },
    {
      icon: Code,
      title: 'Scenario Builder',
      description: 'Visual multi-step test scenarios with drag & drop',
    },
    {
      icon: BarChart3,
      title: 'Real-time Metrics',
      description: 'Live performance charts and detailed analytics',
    },
    {
      icon: Clock,
      title: 'Advanced Configuration',
      description: 'Headers, auth, timeouts, and request customization',
    },
    {
      icon: Shield,
      title: 'Multiple Auth Types',
      description: 'Bearer token, basic auth, and custom headers',
    },
    {
      icon: BarChart3,
      title: 'Detailed Reports',
      description: 'Comprehensive test results with exportable data',
    },
  ];

  return (
    <div 
      className="min-h-screen transition-all duration-300" 
      style={{ backgroundColor: 'var(--bg)', color: 'var(--text)' }}
    >
      <Navigation />

      <div className="max-w-7xl mx-auto px-6 py-16">
        <div className="text-center mb-16">
          <div 
            className="inline-flex items-center gap-2 px-4 py-2 rounded-full mb-6"
            style={{ 
              backgroundColor: 'var(--primary-light)', 
              color: 'var(--primary)' 
            }}
          >
            <Zap className="size-4" />
            <span className="text-sm">Simple. Powerful. Professional.</span>
          </div>
          <h1 className="mb-4 text-4xl font-bold" style={{ color: 'var(--text)' }}>
            Modern API Load Testing
          </h1>
          <p className="text-xl max-w-2xl mx-auto mb-8" style={{ color: 'var(--text-secondary)' }}>
            Test your APIs with ease. Simpler than JMeter, more intuitive than Locust,
            but with all the power you need.
          </p>
          <div className="flex items-center justify-center gap-4">
            <Link to="/create">
              <Button size="lg" className="gap-2 text-white">
                <Plus className="size-5" />
                Create New Test
              </Button>
            </Link>
            <Link to="/history">
              <Button size="lg" variant="outline" className="gap-2 dark:text-white">
                View Test History
              </Button>
            </Link>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-16">
          {features.map((feature) => {
            const Icon = feature.icon;
            return (
              <Card 
                key={feature.title} 
                className="hover:shadow-lg transition-shadow"
                style={{ 
                  borderColor: 'var(--border)',
                  backgroundColor: 'var(--card)'
                }}
              >
                <CardHeader>
                  <div 
                    className="size-12 rounded-xl flex items-center justify-center mb-4"
                    style={{ background: 'linear-gradient(to bottom right, var(--primary), #8b5cf6)' }}
                  >
                    <Icon className="size-6 text-white" />
                  </div>
                  <CardTitle style={{ color: 'var(--text)' }}>{feature.title}</CardTitle>
                  <CardDescription style={{ color: 'var(--text-secondary)' }}>{feature.description}</CardDescription>
                </CardHeader>
              </Card>
            );
          })}
        </div>

        <Card 
          className="border-0 text-white"
          style={{ 
            background: 'linear-gradient(to bottom right, var(--primary), #8b5cf6)'
          }}
        >
          <CardContent className="pt-8 pb-8">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-white mb-2">Ready to test your API?</h3>
                <p className="text-white/90">
                  Create your first load test in less than a minute
                </p>
              </div>
              <Link to="/create">
                <Button size="lg" variant="secondary" className="gap-2">
                  <Plus className="size-5" />
                  Get Started
                </Button>
              </Link>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
