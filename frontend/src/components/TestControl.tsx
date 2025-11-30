import { Card, CardContent } from './ui/card';
import { Button } from './ui/button';
import { Play, Square, Loader2 } from 'lucide-react';

interface TestControlProps {
  isRunning: boolean;
  onStart: () => void;
  onStop: () => void;
}

export function TestControl({ isRunning, onStart, onStop }: TestControlProps) {
  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            {isRunning ? (
              <>
                <div className="relative">
                  <Loader2 className="size-5 text-emerald-600 animate-spin" />
                  <div className="absolute inset-0 bg-emerald-600/20 rounded-full animate-ping" />
                </div>
                <div>
                  <p className="text-emerald-600">Test Running</p>
                  <p className="text-slate-500 text-sm">Load test in progress...</p>
                </div>
              </>
            ) : (
              <>
                <div className="size-5 rounded-full bg-slate-300" />
                <div>
                  <p className="text-slate-700">Ready to Test</p>
                  <p className="text-slate-500 text-sm">Configure and start your test</p>
                </div>
              </>
            )}
          </div>

          <div className="flex gap-3">
            {!isRunning ? (
              <Button onClick={onStart} size="lg" className="gap-2">
                <Play className="size-4" />
                Start Test
              </Button>
            ) : (
              <Button onClick={onStop} variant="destructive" size="lg" className="gap-2">
                <Square className="size-4" />
                Stop Test
              </Button>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
