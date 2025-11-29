import { Navigation } from '../components/Navigation';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select';
import { useTheme } from 'next-themes';
import { useState, useEffect } from 'react';
import { Moon, Sun, Monitor, Save, Trash2, Shield, FileText } from 'lucide-react';
import { HeadersEditor } from '../components/HeadersEditor';

interface AuthPreset {
  id: string;
  name: string;
  type: 'bearer' | 'basic';
  token?: string;
  username?: string;
  password?: string;
}

interface HeadersPreset {
  id: string;
  name: string;
  headers: Array<{ key: string; value: string }>;
}

export function SettingsPage() {
  const { theme, setTheme } = useTheme();
  const [authPresets, setAuthPresets] = useState<AuthPreset[]>([]);
  const [headersPresets, setHeadersPresets] = useState<HeadersPreset[]>([]);
  const [newAuthPreset, setNewAuthPreset] = useState<Partial<AuthPreset>>({
    name: '',
    type: 'bearer',
  });
  const [newHeadersPreset, setNewHeadersPreset] = useState<Partial<HeadersPreset>>({
    name: '',
    headers: [],
  });

  useEffect(() => {
    const savedAuth = localStorage.getItem('authPresets');
    const savedHeaders = localStorage.getItem('headersPresets');
    if (savedAuth) setAuthPresets(JSON.parse(savedAuth));
    if (savedHeaders) setHeadersPresets(JSON.parse(savedHeaders));
  }, []);

  const saveAuthPreset = () => {
    if (!newAuthPreset.name) return;
    const preset: AuthPreset = {
      id: Date.now().toString(),
      name: newAuthPreset.name,
      type: newAuthPreset.type || 'bearer',
      token: newAuthPreset.token,
      username: newAuthPreset.username,
      password: newAuthPreset.password,
    };
    const updated = [...authPresets, preset];
    setAuthPresets(updated);
    localStorage.setItem('authPresets', JSON.stringify(updated));
    setNewAuthPreset({ name: '', type: 'bearer' });
  };

  const deleteAuthPreset = (id: string) => {
    const updated = authPresets.filter((p) => p.id !== id);
    setAuthPresets(updated);
    localStorage.setItem('authPresets', JSON.stringify(updated));
  };

  const saveHeadersPreset = () => {
    if (!newHeadersPreset.name) return;
    const preset: HeadersPreset = {
      id: Date.now().toString(),
      name: newHeadersPreset.name,
      headers: newHeadersPreset.headers || [],
    };
    const updated = [...headersPresets, preset];
    setHeadersPresets(updated);
    localStorage.setItem('headersPresets', JSON.stringify(updated));
    setNewHeadersPreset({ name: '', headers: [] });
  };

  const deleteHeadersPreset = (id: string) => {
    const updated = headersPresets.filter((p) => p.id !== id);
    setHeadersPresets(updated);
    localStorage.setItem('headersPresets', JSON.stringify(updated));
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 transition-colors">
      <Navigation />

      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8">
          <h1 className="text-slate-900 dark:text-slate-100 mb-2">Settings</h1>
          <p className="text-slate-600 dark:text-slate-400">Manage your preferences and presets</p>
        </div>

        <div className="space-y-6">
          {/* Theme Settings */}
          <Card className="shadow-sm border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-slate-900 dark:text-slate-100">
                <Monitor className="size-5" />
                Appearance
              </CardTitle>
              <CardDescription className="text-slate-600 dark:text-slate-400">
                Customize the theme and appearance
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <Label className="text-slate-900 dark:text-slate-100">Theme</Label>
                  <p className="text-sm text-slate-600 dark:text-slate-400">
                    Choose your preferred theme
                  </p>
                </div>
                <div className="flex items-center gap-2">
                  <Button
                    variant={theme === 'light' ? 'default' : 'outline'}
                    size="sm"
                    onClick={() => setTheme('light')}
                    className="gap-2"
                  >
                    <Sun className="size-4" />
                    Light
                  </Button>
                  <Button
                    variant={theme === 'dark' ? 'default' : 'outline'}
                    size="sm"
                    onClick={() => setTheme('dark')}
                    className="gap-2"
                  >
                    <Moon className="size-4" />
                    Dark
                  </Button>
                  <Button
                    variant={theme === 'system' ? 'default' : 'outline'}
                    size="sm"
                    onClick={() => setTheme('system')}
                    className="gap-2"
                  >
                    <Monitor className="size-4" />
                    System
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Auth Presets */}
          <Card className="shadow-sm border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-slate-900 dark:text-slate-100">
                <Shield className="size-5" />
                Authentication Presets
              </CardTitle>
              <CardDescription className="text-slate-600 dark:text-slate-400">
                Save and reuse authentication configurations
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-3 p-4 border border-slate-200 dark:border-slate-800 rounded-lg">
                <Label className="text-slate-900 dark:text-slate-100">Create New Preset</Label>
                <Input
                  placeholder="Preset name"
                  value={newAuthPreset.name || ''}
                  onChange={(e) => setNewAuthPreset({ ...newAuthPreset, name: e.target.value })}
                  className="bg-white dark:bg-slate-900"
                />
                <Select
                  value={newAuthPreset.type || 'bearer'}
                  onValueChange={(value: 'bearer' | 'basic') =>
                    setNewAuthPreset({ ...newAuthPreset, type: value })
                  }
                >
                  <SelectTrigger className="bg-white dark:bg-slate-900">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="bearer">Bearer Token</SelectItem>
                    <SelectItem value="basic">Basic Auth</SelectItem>
                  </SelectContent>
                </Select>
                {newAuthPreset.type === 'bearer' ? (
                  <Input
                    type="password"
                    placeholder="Bearer token"
                    value={newAuthPreset.token || ''}
                    onChange={(e) => setNewAuthPreset({ ...newAuthPreset, token: e.target.value })}
                    className="bg-white dark:bg-slate-900"
                  />
                ) : (
                  <div className="space-y-2">
                    <Input
                      placeholder="Username"
                      value={newAuthPreset.username || ''}
                      onChange={(e) =>
                        setNewAuthPreset({ ...newAuthPreset, username: e.target.value })
                      }
                      className="bg-white dark:bg-slate-900"
                    />
                    <Input
                      type="password"
                      placeholder="Password"
                      value={newAuthPreset.password || ''}
                      onChange={(e) =>
                        setNewAuthPreset({ ...newAuthPreset, password: e.target.value })
                      }
                      className="bg-white dark:bg-slate-900"
                    />
                  </div>
                )}
                <Button onClick={saveAuthPreset} className="w-full gap-2">
                  <Save className="size-4" />
                  Save Preset
                </Button>
              </div>

              <div className="space-y-2">
                {authPresets.map((preset) => (
                  <div
                    key={preset.id}
                    className="flex items-center justify-between p-3 border border-slate-200 dark:border-slate-800 rounded-lg"
                  >
                    <div>
                      <p className="text-slate-900 dark:text-slate-100 font-medium">{preset.name}</p>
                      <p className="text-sm text-slate-600 dark:text-slate-400 capitalize">
                        {preset.type}
                      </p>
                    </div>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => deleteAuthPreset(preset.id)}
                    >
                      <Trash2 className="size-4 text-red-500" />
                    </Button>
                  </div>
                ))}
                {authPresets.length === 0 && (
                  <p className="text-center text-slate-500 dark:text-slate-400 py-4">
                    No presets saved yet
                  </p>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Headers Presets */}
          <Card className="shadow-sm border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-slate-900 dark:text-slate-100">
                <FileText className="size-5" />
                Headers Presets
              </CardTitle>
              <CardDescription className="text-slate-600 dark:text-slate-400">
                Save and reuse header configurations
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-3 p-4 border border-slate-200 dark:border-slate-800 rounded-lg">
                <Label className="text-slate-900 dark:text-slate-100">Create New Preset</Label>
                <Input
                  placeholder="Preset name"
                  value={newHeadersPreset.name || ''}
                  onChange={(e) =>
                    setNewHeadersPreset({ ...newHeadersPreset, name: e.target.value })
                  }
                  className="bg-white dark:bg-slate-900"
                />
                <HeadersEditor
                  headers={newHeadersPreset.headers || []}
                  onChange={(headers) => setNewHeadersPreset({ ...newHeadersPreset, headers })}
                  disabled={false}
                />
                <Button onClick={saveHeadersPreset} className="w-full gap-2">
                  <Save className="size-4" />
                  Save Preset
                </Button>
              </div>

              <div className="space-y-2">
                {headersPresets.map((preset) => (
                  <div
                    key={preset.id}
                    className="flex items-center justify-between p-3 border border-slate-200 dark:border-slate-800 rounded-lg"
                  >
                    <div>
                      <p className="text-slate-900 dark:text-slate-100 font-medium">{preset.name}</p>
                      <p className="text-sm text-slate-600 dark:text-slate-400">
                        {preset.headers.length} header(s)
                      </p>
                    </div>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => deleteHeadersPreset(preset.id)}
                    >
                      <Trash2 className="size-4 text-red-500" />
                    </Button>
                  </div>
                ))}
                {headersPresets.length === 0 && (
                  <p className="text-center text-slate-500 dark:text-slate-400 py-4">
                    No presets saved yet
                  </p>
                )}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}

