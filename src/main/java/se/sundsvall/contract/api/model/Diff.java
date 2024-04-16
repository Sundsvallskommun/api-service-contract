package se.sundsvall.contract.api.model;

import java.util.List;

import se.sundsvall.contract.model.Change;

public record Diff(Integer oldVersion, Integer newVersion, List<Change> changes, List<Integer> availableVersions) { }
