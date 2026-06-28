package com.manusrao.nano.model;

import java.util.List;

public record DetectionRule<T>(String keyword, T value, List<String> excludes) {}
