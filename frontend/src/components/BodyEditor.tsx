import { Textarea } from './ui/textarea';
import { Label } from './ui/label';

interface BodyEditorProps {
  body: string;
  onChange: (body: string) => void;
  disabled: boolean;
}

export function BodyEditor({ body, onChange, disabled }: BodyEditorProps) {
  return (
    <div className="space-y-2">
      <Label className="text-slate-600 dark:text-slate-400 text-sm">JSON Body</Label>
      <Textarea
        placeholder='{\n  "key": "value"\n}'
        value={body}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
        className="font-mono text-sm min-h-[200px] bg-slate-50 dark:bg-slate-950 border-slate-200 dark:border-slate-700 text-slate-900 dark:text-slate-100"
      />
      <p className="text-xs text-slate-500 dark:text-slate-500">Enter JSON request body</p>
    </div>
  );
}
