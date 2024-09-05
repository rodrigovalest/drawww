package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.repositories.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public String getRandomTheme() {
        return this.themeRepository.findRandomTheme().getName();
    }
}
