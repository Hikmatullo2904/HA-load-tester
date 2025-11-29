import { Button } from './ui/button';
import { Input } from './ui/input';
import { Plus, X } from 'lucide-react';

interface HeadersEditorProps {
  headers: Array<{ key: string; value: string }>;
  onChange: (headers: Array<{ key: string; value: string }>) => void;
  disabled: boolean;
}

export function HeadersEditor({ headers, onChange, disabled }: HeadersEditorProps) {
  const addHeader = () => {
    onChange([...headers, { key: '', value: '' }]);
  };

  const removeHeader = (index: number) => {
    onChange(headers.filter((_, i) => i !== index));
  };

  const updateHeader = (index: number, field: 'key' | 'value', value: string) => {
    const newHeaders = [...headers];
    newHeaders[index][field] = value;
    onChange(newHeaders);
  };

  return (
    <div className="space-y-3">
      {headers.map((header, index) => (
        <div key={index} className="flex items-center gap-2">
          <Input
            placeholder="Header name"
            value={header.key}
            onChange={(e) => updateHeader(index, 'key', e.target.value)}
            disabled={disabled}
            className="flex-1 bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
          />
          <Input
            placeholder="Header value"
            value={header.value}
            onChange={(e) => updateHeader(index, 'value', e.target.value)}
            disabled={disabled}
            className="flex-1 bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700"
          />
          <Button
            variant="ghost"
            size="icon"
            onClick={() => removeHeader(index)}
            disabled={disabled}
          >
            <X className="size-4" />
          </Button>
        </div>
      ))}
      <Button
        variant="outline"
        size="sm"
        onClick={addHeader}
        disabled={disabled}
        className="w-full gap-2"
      >
        <Plus className="size-4" />
        Add Header
      </Button>
    </div>
  );
}
