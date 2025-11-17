package uz.hikmatullo.loadtesting.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import uz.hikmatullo.loadtesting.model.enums.WorkerStatusEnum;

import java.time.Instant;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class WorkerStatus {

    private Instant lastHeartbeat;
    private double cpuLoad;
    private long freeMemory;
    private int activeTasks;

    private WorkerStatusEnum status;


}
