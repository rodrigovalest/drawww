package com.rodrigo.drawing_contest.models.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRedis {
    private Long id;
    private String username;
}
