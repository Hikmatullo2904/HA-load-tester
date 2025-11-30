import { TestConfig } from '../App';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Label } from './ui/label';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Globe, Users, Clock, TrendingUp, Zap } from 'lucide-react';

interface TestConfigurationProps {
  config: TestConfig;
  onConfigChange: (config: TestConfig) => void;
  disabled: boolean;
}

export function TestConfiguration({ config, onConfigChange, disabled }: TestConfigurationProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Test Configuration</CardTitle>
        <CardDescription>Configure your load test parameters</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="url" className="flex items-center gap-2">
            <Globe className="size-4" />
            Target URL
          </Label>
          <Input
            id="url"
            type="url"
            placeholder="https://api.example.com/endpoint"
            value={config.url}
            onChange={(e) => onConfigChange({ ...config, url: e.target.value })}
            disabled={disabled}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="method">HTTP Method</Label>
          <Select
            value={config.method}
            onValueChange={(value) => onConfigChange({ ...config, method: value })}
            disabled={disabled}
          >
            <SelectTrigger id="method">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="GET">GET</SelectItem>
              <SelectItem value="POST">POST</SelectItem>
              <SelectItem value="PUT">PUT</SelectItem>
              <SelectItem value="DELETE">DELETE</SelectItem>
              <SelectItem value="PATCH">PATCH</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div className="space-y-2">
          <Label htmlFor="testType" className="flex items-center gap-2">
            <Zap className="size-4" />
            Test Type
          </Label>
          <Select
            value={config.testType}
            onValueChange={(value) => onConfigChange({ ...config, testType: value })}
            disabled={disabled}
          >
            <SelectTrigger id="testType">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="fixed">Fixed Load</SelectItem>
              <SelectItem value="ramp-up">Ramp-up</SelectItem>
              <SelectItem value="spike">Spike Test</SelectItem>
              <SelectItem value="step">Step Load</SelectItem>
            </SelectContent>
          </Select>
          <p className="text-slate-500 text-sm">
            {config.testType === 'fixed' && 'Constant load throughout the test'}
            {config.testType === 'ramp-up' && 'Gradually increase load to target'}
            {config.testType === 'spike' && 'Sudden spike in the middle of test'}
            {config.testType === 'step' && 'Incremental load increases'}
          </p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="users" className="flex items-center gap-2">
            <Users className="size-4" />
            Virtual Users
          </Label>
          <Input
            id="users"
            type="number"
            min="1"
            max="10000"
            value={config.virtualUsers}
            onChange={(e) =>
              onConfigChange({ ...config, virtualUsers: parseInt(e.target.value) || 1 })
            }
            disabled={disabled}
          />
          <p className="text-slate-500 text-sm">Number of concurrent users</p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="duration" className="flex items-center gap-2">
            <Clock className="size-4" />
            Duration (seconds)
          </Label>
          <Input
            id="duration"
            type="number"
            min="1"
            max="3600"
            value={config.duration}
            onChange={(e) =>
              onConfigChange({ ...config, duration: parseInt(e.target.value) || 1 })
            }
            disabled={disabled}
          />
          <p className="text-slate-500 text-sm">Test duration in seconds</p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="rampup" className="flex items-center gap-2">
            <TrendingUp className="size-4" />
            Ramp-up Time (seconds)
          </Label>
          <Input
            id="rampup"
            type="number"
            min="0"
            max="600"
            value={config.rampUpTime}
            onChange={(e) =>
              onConfigChange({ ...config, rampUpTime: parseInt(e.target.value) || 0 })
            }
            disabled={disabled || config.testType !== 'ramp-up'}
          />
          <p className="text-slate-500 text-sm">
            {config.testType === 'ramp-up' 
              ? 'Time to reach full load'
              : 'Only applies to ramp-up test type'}
          </p>
        </div>
      </CardContent>
    </Card>
  );
}