package com.wsep202.TradingSystem.dynamic_start_up;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitialSystem {

    List<ActivityDefinition> activities;
}
