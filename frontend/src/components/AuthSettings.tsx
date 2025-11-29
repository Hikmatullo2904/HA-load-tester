import { TestConfig } from '../types/test';
import { Label } from './ui/label';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';

interface AuthSettingsProps {
  config: TestConfig;
  onChange: (config: TestConfig) => void;
  disabled: boolean;
}

export function AuthSettings({ config, onChange, disabled }: AuthSettingsProps) {
  return (
    <div className="space-y-4">
      <div className="space-y-2">
        <Label className="text-slate-700 dark:text-slate-300">Authentication Type</Label>
        <Select
          value={config.authType}
          onValueChange={(value: 'none' | 'bearer' | 'basic') =>
            onChange({ ...config, authType: value })
          }
          disabled={disabled}
        >
          <SelectTrigger className="bg-white dark:bg-slate-900">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="none">None</SelectItem>
            <SelectItem value="bearer">Bearer Token</SelectItem>
            <SelectItem value="basic">Basic Auth</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {config.authType === 'bearer' && (
        <div className="space-y-2">
          <Label htmlFor="token" className="text-slate-700 dark:text-slate-300">Bearer Token</Label>
          <Input
            id="token"
            type="password"
            placeholder="Enter your bearer token"
            value={config.authToken || ''}
            onChange={(e) => onChange({ ...config, authToken: e.target.value })}
            disabled={disabled}
            className="bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
          />
        </div>
      )}

      {config.authType === 'basic' && (
        <>
          <div className="space-y-2">
            <Label htmlFor="username" className="text-slate-700 dark:text-slate-300">Username</Label>
            <Input
              id="username"
              placeholder="Enter username"
              value={config.authUsername || ''}
              onChange={(e) => onChange({ ...config, authUsername: e.target.value })}
              disabled={disabled}
              className="bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="password" className="text-slate-700 dark:text-slate-300">Password</Label>
            <Input
              id="password"
              type="password"
              placeholder="Enter password"
              value={config.authPassword || ''}
              onChange={(e) => onChange({ ...config, authPassword: e.target.value })}
              disabled={disabled}
              className="bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
            />
          </div>
        </>
      )}
    </div>
  );
}
