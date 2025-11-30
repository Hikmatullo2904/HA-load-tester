import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Textarea } from './ui/textarea';
import { Plus, GripVertical, Trash2, List, ChevronDown, ChevronUp, X } from 'lucide-react';
import { TestStep } from '../types/test';
import { useState } from 'react';
import { HeadersEditor } from './HeadersEditor';

interface ScenarioBuilderProps {
  steps: TestStep[];
  onStepsChange: (steps: TestStep[]) => void;
  disabled: boolean;
}

export function ScenarioBuilder({ steps, onStepsChange, disabled }: ScenarioBuilderProps) {
  const [expandedStep, setExpandedStep] = useState<string | null>(null);
  const [draggedIndex, setDraggedIndex] = useState<number | null>(null);

  const addStep = () => {
    const newStep: TestStep = {
      id: Date.now().toString(),
      name: `Step ${steps.length + 1}`,
      method: 'GET',
      url: '',
      weight: 1,
      delay: 0,
      headers: [],
      body: '',
      expectedStatus: 200,
      retryCount: 0,
    };
    onStepsChange([...steps, newStep]);
    setExpandedStep(newStep.id);
  };

  const removeStep = (id: string) => {
    onStepsChange(steps.filter((s) => s.id !== id));
    if (expandedStep === id) {
      setExpandedStep(null);
    }
  };

  const updateStep = (id: string, updates: Partial<TestStep>) => {
    onStepsChange(steps.map((s) => (s.id === id ? { ...s, ...updates } : s)));
  };

  const handleDragStart = (index: number) => {
    setDraggedIndex(index);
  };

  const handleDragOver = (e: React.DragEvent, index: number) => {
    e.preventDefault();
    if (draggedIndex === null || draggedIndex === index) return;

    const newSteps = [...steps];
    const draggedItem = newSteps[draggedIndex];
    newSteps.splice(draggedIndex, 1);
    newSteps.splice(index, 0, draggedItem);
    onStepsChange(newSteps);
    setDraggedIndex(index);
  };

  const handleDragEnd = () => {
    setDraggedIndex(null);
  };

  const moveStep = (index: number, direction: 'up' | 'down') => {
    const newSteps = [...steps];
    const newIndex = direction === 'up' ? index - 1 : index + 1;
    if (newIndex < 0 || newIndex >= steps.length) return;
    [newSteps[index], newSteps[newIndex]] = [newSteps[newIndex], newSteps[index]];
    onStepsChange(newSteps);
  };

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
          <List className="size-5" style={{ color: 'var(--chart-5)' }} />
          Test Scenarios
        </CardTitle>
        <CardDescription style={{ color: 'var(--text-secondary)' }}>
          Build multi-step test scenarios with drag & drop
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        {steps.length === 0 ? (
          <div className="text-center py-8" style={{ color: 'var(--text-secondary)' }}>
            <List className="size-12 mx-auto mb-3" style={{ color: 'var(--text-muted)' }} />
            <p className="mb-4">No steps added yet</p>
            <Button onClick={addStep} disabled={disabled} variant="outline" className="gap-2">
              <Plus className="size-4" />
              Add First Step
            </Button>
          </div>
        ) : (
          <>
            {steps.map((step, index) => (
              <div
                key={step.id}
                draggable={!disabled}
                onDragStart={() => handleDragStart(index)}
                onDragOver={(e) => handleDragOver(e, index)}
                onDragEnd={handleDragEnd}
                className={`border rounded-lg p-4 space-y-3 transition-all ${
                  draggedIndex === index ? 'opacity-50' : 'hover:shadow-md'
                } ${disabled ? 'cursor-default' : 'cursor-move'}`}
                style={{ 
                  borderColor: 'var(--border)',
                  backgroundColor: 'var(--bg-secondary)'
                }}
              >
                <div className="flex items-center gap-2">
                  <GripVertical
                    className={`size-4 ${
                      disabled ? 'cursor-default' : 'cursor-move'
                    }`}
                    style={{ color: 'var(--text-muted)' }}
                  />
                  <Input
                    placeholder="Step name"
                    value={step.name}
                    onChange={(e) => updateStep(step.id, { name: e.target.value })}
                    disabled={disabled}
                    className="flex-1"
                    style={{ 
                      backgroundColor: 'var(--input-background)', 
                      borderColor: 'var(--input-border)',
                      color: 'var(--text)'
                    }}
                  />
                  <div className="flex items-center gap-1">
                    {index > 0 && (
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => moveStep(index, 'up')}
                        disabled={disabled}
                        className="size-8"
                      >
                        <ChevronUp className="size-4" />
                      </Button>
                    )}
                    {index < steps.length - 1 && (
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => moveStep(index, 'down')}
                        disabled={disabled}
                        className="size-8"
                      >
                        <ChevronDown className="size-4" />
                      </Button>
                    )}
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => removeStep(step.id)}
                      disabled={disabled}
                      className="size-8"
                    >
                      <Trash2 className="size-4 text-red-500 dark:text-red-400" />
                    </Button>
                  </div>
                </div>

                {expandedStep === step.id && (
                  <div 
                    className="space-y-4 pt-3 border-t"
                    style={{ borderColor: 'var(--border)' }}
                  >
                    <div className="grid grid-cols-3 gap-3">
                      <div className="space-y-1">
                        <Label className="text-xs" style={{ color: 'var(--text-secondary)' }}>Method</Label>
                        <Select
                          value={step.method}
                          onValueChange={(value) => updateStep(step.id, { method: value })}
                          disabled={disabled}
                        >
                          <SelectTrigger 
                            className="h-8 text-sm"
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
                      <div className="space-y-1">
                        <Label className="text-xs" style={{ color: 'var(--text-secondary)' }}>Weight</Label>
                        <Input
                          type="number"
                          min="1"
                          value={step.weight || 1}
                          onChange={(e) =>
                            updateStep(step.id, { weight: parseInt(e.target.value) || 1 })
                          }
                          disabled={disabled}
                          className="h-8 text-sm"
                          style={{ 
                            backgroundColor: 'var(--input-background)', 
                            borderColor: 'var(--input-border)',
                            color: 'var(--text)'
                          }}
                        />
                      </div>
                      <div className="space-y-1">
                        <Label className="text-xs" style={{ color: 'var(--text-secondary)' }}>Delay (ms)</Label>
                        <Input
                          type="number"
                          min="0"
                          value={step.delay || 0}
                          onChange={(e) =>
                            updateStep(step.id, { delay: parseInt(e.target.value) || 0 })
                          }
                          disabled={disabled}
                          className="h-8 text-sm"
                          style={{ 
                            backgroundColor: 'var(--input-background)', 
                            borderColor: 'var(--input-border)',
                            color: 'var(--text)'
                          }}
                        />
                      </div>
                    </div>

                    <div className="space-y-1">
                      <Label className="text-xs" style={{ color: 'var(--text-secondary)' }}>URL</Label>
                      <Input
                        placeholder="https://api.example.com/endpoint"
                        value={step.url}
                        onChange={(e) => updateStep(step.id, { url: e.target.value })}
                        disabled={disabled}
                        className="h-8 text-sm"
                        style={{ 
                          backgroundColor: 'var(--input-background)', 
                          borderColor: 'var(--input-border)',
                          color: 'var(--text)'
                        }}
                      />
                    </div>

                    {(step.method === 'POST' || step.method === 'PUT' || step.method === 'PATCH') && (
                      <div className="space-y-1">
                        <Label className="text-xs" style={{ color: 'var(--text-secondary)' }}>Request Body</Label>
                        <Textarea
                          placeholder='{\n  "key": "value"\n}'
                          value={step.body || ''}
                          onChange={(e) => updateStep(step.id, { body: e.target.value })}
                          disabled={disabled}
                          className="font-mono text-sm min-h-[120px]"
                          style={{ 
                            backgroundColor: 'var(--input-background)', 
                            borderColor: 'var(--input-border)',
                            color: 'var(--text)'
                          }}
                        />
                      </div>
                    )}

                    <div className="space-y-2">
                      <Label className="text-xs" style={{ color: 'var(--text-secondary)' }}>Headers</Label>
                      <HeadersEditor
                        headers={step.headers || []}
                        onChange={(headers) => updateStep(step.id, { headers })}
                        disabled={disabled}
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                      <div className="space-y-1">
                        <Label className="text-xs" style={{ color: 'var(--text-secondary)' }}>
                          Expected Status
                        </Label>
                        <Input
                          type="number"
                          min="100"
                          max="599"
                          value={step.expectedStatus || 200}
                          onChange={(e) =>
                            updateStep(step.id, { expectedStatus: parseInt(e.target.value) || 200 })
                          }
                          disabled={disabled}
                          className="h-8 text-sm"
                          style={{ 
                            backgroundColor: 'var(--input-background)', 
                            borderColor: 'var(--input-border)',
                            color: 'var(--text)'
                          }}
                        />
                      </div>
                      <div className="space-y-1">
                        <Label className="text-xs" style={{ color: 'var(--text-secondary)' }}>Retry Count</Label>
                        <Input
                          type="number"
                          min="0"
                          max="5"
                          value={step.retryCount || 0}
                          onChange={(e) =>
                            updateStep(step.id, { retryCount: parseInt(e.target.value) || 0 })
                          }
                          disabled={disabled}
                          className="h-8 text-sm"
                          style={{ 
                            backgroundColor: 'var(--input-background)', 
                            borderColor: 'var(--input-border)',
                            color: 'var(--text)'
                          }}
                        />
                      </div>
                    </div>
                  </div>
                )}

                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => setExpandedStep(expandedStep === step.id ? null : step.id)}
                  className="w-full text-xs"
                >
                  {expandedStep === step.id ? 'Collapse' : 'Expand'}
                </Button>
              </div>
            ))}

            <Button
              onClick={addStep}
              disabled={disabled}
              variant="outline"
              size="sm"
              className="w-full gap-2"
            >
              <Plus className="size-4" />
              Add Step
            </Button>
          </>
        )}
      </CardContent>
    </Card>
  );
}
