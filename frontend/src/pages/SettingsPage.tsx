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
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 transition-all duration-300" style={{ backgroundColor: 'var(--bg)' }}>
      <Navigation />

      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8">
          <h1 className="mb-2 text-3xl font-bold" style={{ color: 'var(--text)' }}>Settings</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Manage your preferences and presets</p>
        </div>

        <div className="space-y-6">
          {/* Theme Settings */}
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
                <Monitor className="size-5" />
                Appearance
              </CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Customize the theme and appearance
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <Label style={{ color: 'var(--text)' }}>Theme</Label>
                  <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>
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
                <Shield className="size-5" />
                Authentication Presets
              </CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Save and reuse authentication configurations
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div 
                className="space-y-3 p-4 border rounded-lg"
                style={{ borderColor: 'var(--border)' }}
              >
                <Label style={{ color: 'var(--text)' }}>Create New Preset</Label>
                <Input
                  placeholder="Preset name"
                  value={newAuthPreset.name || ''}
                  onChange={(e) => setNewAuthPreset({ ...newAuthPreset, name: e.target.value })}
                  style={{ 
                    backgroundColor: 'var(--input-background)', 
                    borderColor: 'var(--input-border)',
                    color: 'var(--text)'
                  }}
                />
                <Select
                  value={newAuthPreset.type || 'bearer'}
                  onValueChange={(value: 'bearer' | 'basic') =>
                    setNewAuthPreset({ ...newAuthPreset, type: value })
                  }
                >
                  <SelectTrigger
                    style={{ 
                      backgroundColor: 'var(--input-background)', 
                      borderColor: 'var(--input-border)',
                      color: 'var(--text)'
                    }}
                  >
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
                    style={{ 
                      backgroundColor: 'var(--input-background)', 
                      borderColor: 'var(--input-border)',
                      color: 'var(--text)'
                    }}
                  />
                ) : (
                  <div className="space-y-2">
                    <Input
                      placeholder="Username"
                      value={newAuthPreset.username || ''}
                      onChange={(e) =>
                        setNewAuthPreset({ ...newAuthPreset, username: e.target.value })
                      }
                      style={{ 
                        backgroundColor: 'var(--input-background)', 
                        borderColor: 'var(--input-border)',
                        color: 'var(--text)'
                      }}
                    />
                    <Input
                      type="password"
                      placeholder="Password"
                      value={newAuthPreset.password || ''}
                      onChange={(e) =>
                        setNewAuthPreset({ ...newAuthPreset, password: e.target.value })
                      }
                      style={{ 
                        backgroundColor: 'var(--input-background)', 
                        borderColor: 'var(--input-border)',
                        color: 'var(--text)'
                      }}
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
                    className="flex items-center justify-between p-3 border rounded-lg"
                    style={{ borderColor: 'var(--border)' }}
                  >
                    <div>
                      <p className="font-medium" style={{ color: 'var(--text)' }}>{preset.name}</p>
                      <p className="text-sm capitalize" style={{ color: 'var(--text-secondary)' }}>
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
                  <p className="text-center py-4" style={{ color: 'var(--text-muted)' }}>
                    No presets saved yet
                  </p>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Headers Presets */}
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
                <FileText className="size-5" />
                Headers Presets
              </CardTitle>
              <CardDescription style={{ color: 'var(--text-secondary)' }}>
                Save and reuse header configurations
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div 
                className="space-y-3 p-4 border rounded-lg"
                style={{ borderColor: 'var(--border)' }}
              >
                <Label style={{ color: 'var(--text)' }}>Create New Preset</Label>
                <Input
                  placeholder="Preset name"
                  value={newHeadersPreset.name || ''}
                  onChange={(e) =>
                    setNewHeadersPreset({ ...newHeadersPreset, name: e.target.value })
                  }
                  style={{ 
                    backgroundColor: 'var(--input-background)', 
                    borderColor: 'var(--input-border)',
                    color: 'var(--text)'
                  }}
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
                    className="flex items-center justify-between p-3 border rounded-lg"
                    style={{ borderColor: 'var(--border)' }}
                  >
                    <div>
                      <p className="font-medium" style={{ color: 'var(--text)' }}>{preset.name}</p>
                      <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>
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
                  <p className="text-center py-4" style={{ color: 'var(--text-muted)' }}>
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

