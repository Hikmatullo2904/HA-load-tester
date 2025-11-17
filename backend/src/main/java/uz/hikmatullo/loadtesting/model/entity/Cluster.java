package uz.hikmatullo.loadtesting.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cluster extends BaseEntity {
    private String name;
    private String description;

}