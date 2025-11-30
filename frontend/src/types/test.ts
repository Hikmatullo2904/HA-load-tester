export interface TestConfig {
  url: string;
  method: string;
  testType: string;
  virtualUsers: number;
  duration: number;
  rampUpTime: number;
  headers: Array<{ key: string; value: string }>;
  body: string;
  authType: 'none' | 'bearer' | 'basic';
  authToken?: string;
  authUsername?: string;
  authPassword?: string;
  timeout: number;
  keepAlive: boolean;
  connectionReuse: boolean;
  delayBetweenRequests: number;
}

export interface TestStep {
  id: string;
  name: string;
  method: string;
  url: string;
  body?: string;
  headers?: Array<{ key: string; value: string }>;
  weight: number;
  delay?: number;
  expectedStatus?: number;
  retryCount?: number;
  condition?: string;
}

export interface TestMetrics {
  totalRequests: number;
  successfulRequests: number;
  failedRequests: number;
  averageResponseTime: number;
  minResponseTime: number;
  maxResponseTime: number;
  p50ResponseTime: number;
  p95ResponseTime: number;
  p99ResponseTime: number;
  requestsPerSecond: number;
  errorRate: number;
}

export interface TestResult {
  id: string;
  name: string;
  config: TestConfig;
  steps: TestStep[];
  metrics: TestMetrics;
  responseTimeData: Array<{ time: number; responseTime: number }>;
  activeUsersData: Array<{ time: number; users: number; rps: number }>;
  percentileData: Array<{ percentile: string; value: number }>;
  errorTimelineData?: Array<{ time: number; errors: number }>;
  logs?: Array<{ timestamp: Date; level: 'info' | 'error' | 'warning'; message: string }>;
  startTime: Date;
  endTime: Date;
  status: 'running' | 'completed' | 'failed';
}
