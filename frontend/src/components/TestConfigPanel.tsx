import { TestConfig } from '../types/test';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Label } from './ui/label';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from './ui/accordion';
import { Switch } from './ui/switch';
import { Globe, Zap, Users, Clock, Shield, Settings } from 'lucide-react';
import { HeadersEditor } from './HeadersEditor';
import { BodyEditor } from './BodyEditor';
import { AuthSettings } from './AuthSettings';

interface TestConfigPanelProps {
  config: TestConfig;
  onConfigChange: (config: TestConfig) => void;
  disabled: boolean;
}

export function TestConfigPanel({ config, onConfigChange, disabled }: TestConfigPanelProps) {
  return (
    <Card className="shadow-sm border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
      <CardHeader>
        <CardTitle className="flex items-center gap-2 text-slate-900 dark:text-slate-100">
          <Settings className="size-5 text-blue-600 dark:text-blue-400" />
          Test Configuration
        </CardTitle>
        <CardDescription className="text-slate-600 dark:text-slate-400">Configure your load test parameters</CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="url" className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
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
              className="bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="method" className="text-slate-700 dark:text-slate-300">HTTP Method</Label>
              <Select
                value={config.method}
                onValueChange={(value) => onConfigChange({ ...config, method: value })}
                disabled={disabled}
              >
                <SelectTrigger id="method" className="bg-white dark:bg-slate-900">
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
              <Label htmlFor="testType" className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
                <Zap className="size-4" />
                Test Type
              </Label>
              <Select
                value={config.testType}
                onValueChange={(value) => onConfigChange({ ...config, testType: value })}
                disabled={disabled}
              >
                <SelectTrigger id="testType" className="bg-white dark:bg-slate-900">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="fixed">Fixed Load</SelectItem>
                  <SelectItem value="ramp-up">Ramp-up Load</SelectItem>
                  <SelectItem value="spike">Spike Test</SelectItem>
                  <SelectItem value="stress">Stress Test</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="users" className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
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
              className="bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="duration" className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
                <Clock className="size-4" />
                Duration (s)
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
                className="bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="rampup" className="text-slate-700 dark:text-slate-300">Ramp-up (s)</Label>
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
                className="bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
              />
            </div>
          </div>
        </div>

        <Accordion type="multiple" className="border-t border-slate-200 dark:border-slate-800 pt-4">
          <AccordionItem value="headers" className="border-slate-200 dark:border-slate-800">
            <AccordionTrigger className="text-slate-700 dark:text-slate-300 hover:no-underline">
              Request Headers
            </AccordionTrigger>
            <AccordionContent>
              <HeadersEditor
                headers={config.headers}
                onChange={(headers) => onConfigChange({ ...config, headers })}
                disabled={disabled}
              />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="body" className="border-slate-200 dark:border-slate-800">
            <AccordionTrigger className="text-slate-700 dark:text-slate-300 hover:no-underline">
              Request Body
            </AccordionTrigger>
            <AccordionContent>
              <BodyEditor
                body={config.body}
                onChange={(body) => onConfigChange({ ...config, body })}
                disabled={disabled}
              />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="auth" className="border-slate-200 dark:border-slate-800">
            <AccordionTrigger className="text-slate-700 dark:text-slate-300 hover:no-underline flex items-center gap-2">
              <Shield className="size-4" />
              Authentication
            </AccordionTrigger>
            <AccordionContent>
              <AuthSettings
                config={config}
                onChange={onConfigChange}
                disabled={disabled}
              />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="advanced" className="border-slate-200 dark:border-slate-800">
            <AccordionTrigger className="text-slate-700 dark:text-slate-300 hover:no-underline">
              Advanced Settings
            </AccordionTrigger>
            <AccordionContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="timeout" className="text-slate-700 dark:text-slate-300">Timeout (seconds)</Label>
                <Input
                  id="timeout"
                  type="number"
                  min="1"
                  max="300"
                  value={config.timeout}
                  onChange={(e) =>
                    onConfigChange({ ...config, timeout: parseInt(e.target.value) || 30 })
                  }
                  disabled={disabled}
                  className="bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="delay" className="text-slate-700 dark:text-slate-300">Delay Between Requests (ms)</Label>
                <Input
                  id="delay"
                  type="number"
                  min="0"
                  max="10000"
                  value={config.delayBetweenRequests}
                  onChange={(e) =>
                    onConfigChange({ ...config, delayBetweenRequests: parseInt(e.target.value) || 0 })
                  }
                  disabled={disabled}
                  className="bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
                />
              </div>

              <div className="flex items-center justify-between">
                <Label htmlFor="keepalive" className="text-slate-700 dark:text-slate-300">Keep-Alive</Label>
                <Switch
                  id="keepalive"
                  checked={config.keepAlive}
                  onCheckedChange={(checked) => onConfigChange({ ...config, keepAlive: checked })}
                  disabled={disabled}
                />
              </div>

              <div className="flex items-center justify-between">
                <Label htmlFor="reuse" className="text-slate-700 dark:text-slate-300">Connection Reuse</Label>
                <Switch
                  id="reuse"
                  checked={config.connectionReuse}
                  onCheckedChange={(checked) =>
                    onConfigChange({ ...config, connectionReuse: checked })
                  }
                  disabled={disabled}
                />
              </div>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      </CardContent>
    </Card>
  );
}
