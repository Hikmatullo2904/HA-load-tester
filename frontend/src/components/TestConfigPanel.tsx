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
          <Settings className="size-5" style={{ color: 'var(--primary)' }} />
          Test Configuration
        </CardTitle>
        <CardDescription style={{ color: 'var(--text-secondary)' }}>
          Configure your load test parameters
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="url" className="flex items-center gap-2" style={{ color: 'var(--text)' }}>
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
              style={{
                backgroundColor: 'var(--input-background)',
                borderColor: 'var(--input-border)',
                color: 'var(--text)'
              }}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="method" style={{ color: 'var(--text)' }}>HTTP Method</Label>
              <Select
                value={config.method}
                onValueChange={(value: string) => onConfigChange({ ...config, method: value })}
                disabled={disabled}
              >
                <SelectTrigger
                  id="method"
                  style={{
                    backgroundColor: 'var(--input-background)',
                    borderColor: 'var(--input-border)',
                    color: 'var(--text)'
                  }}
                >
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
              <Label htmlFor="testType" className="flex items-center gap-2" style={{ color: 'var(--text)' }}>
                <Zap className="size-4" />
                Test Type
              </Label>
              <Select
                value={config.testType}
                onValueChange={(value: string) => onConfigChange({ ...config, testType: value })}
                disabled={disabled}
              >
                <SelectTrigger
                  id="testType"
                  style={{
                    backgroundColor: 'var(--input-background)',
                    borderColor: 'var(--input-border)',
                    color: 'var(--text)'
                  }}
                >
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
            <Label htmlFor="users" className="flex items-center gap-2" style={{ color: 'var(--text)' }}>
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
              style={{
                backgroundColor: 'var(--input-background)',
                borderColor: 'var(--input-border)',
                color: 'var(--text)'
              }}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="duration" className="flex items-center gap-2" style={{ color: 'var(--text)' }}>
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
                style={{
                  backgroundColor: 'var(--input-background)',
                  borderColor: 'var(--input-border)',
                  color: 'var(--text)'
                }}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="rampup" style={{ color: 'var(--text)' }}>Ramp-up (s)</Label>
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
                style={{
                  backgroundColor: 'var(--input-background)',
                  borderColor: 'var(--input-border)',
                  color: 'var(--text)'
                }}
              />
            </div>
          </div>
        </div>

        <Accordion
          type="multiple"
          className="border-t pt-4"
          style={{ borderColor: 'var(--border)' }}
        >
          <AccordionItem value="headers" style={{ borderColor: 'var(--border)' }}>
            <AccordionTrigger className="hover:no-underline" style={{ color: 'var(--text)' }}>
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

          <AccordionItem value="body" style={{ borderColor: 'var(--border)' }}>
            <AccordionTrigger className="hover:no-underline" style={{ color: 'var(--text)' }}>
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

          <AccordionItem value="auth" style={{ borderColor: 'var(--border)' }}>
            <AccordionTrigger className="hover:no-underline flex items-center gap-2" style={{ color: 'var(--text)' }}>
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

          <AccordionItem value="advanced" style={{ borderColor: 'var(--border)' }}>
            <AccordionTrigger className="hover:no-underline" style={{ color: 'var(--text)' }}>
              Advanced Settings
            </AccordionTrigger>
            <AccordionContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="timeout" style={{ color: 'var(--text)' }}>Timeout (seconds)</Label>
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
                  style={{
                    backgroundColor: 'var(--input-background)',
                    borderColor: 'var(--input-border)',
                    color: 'var(--text)'
                  }}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="delay" style={{ color: 'var(--text)' }}>Delay Between Requests (ms)</Label>
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
                  style={{
                    backgroundColor: 'var(--input-background)',
                    borderColor: 'var(--input-border)',
                    color: 'var(--text)'
                  }}
                />
              </div>

              <div className="flex items-center justify-between">
                <Label htmlFor="keepalive" style={{ color: 'var(--text)' }}>Keep-Alive</Label>
                <Switch
                  id="keepalive"
                  checked={config.keepAlive}
                  onCheckedChange={(checked: boolean) => onConfigChange({ ...config, keepAlive: checked })}
                  disabled={disabled}
                />
              </div>

              <div className="flex items-center justify-between">
                <Label htmlFor="reuse" style={{ color: 'var(--text)' }}>Connection Reuse</Label>
                <Switch
                  id="reuse"
                  checked={config.connectionReuse}
                  onCheckedChange={(checked: boolean) =>
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
