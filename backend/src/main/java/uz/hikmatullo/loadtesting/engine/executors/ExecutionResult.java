package uz.hikmatullo.loadtesting.engine.executors;

import uz.hikmatullo.loadtesting.model.entity.metrics.RequestMetrics;

import java.util.List;

public record ExecutionResult(List<RequestMetrics> metrics, long startedAt, long finishedAt) {}