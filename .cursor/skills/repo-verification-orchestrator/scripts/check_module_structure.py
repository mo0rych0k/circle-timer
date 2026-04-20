#!/usr/bin/env python3

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[4]
SETTINGS_GRADLE = REPO_ROOT / "settings.gradle.kts"
COMPOSE_APP_BUILD = REPO_ROOT / "composeApp" / "build.gradle.kts"
APP_MODULES = (
    REPO_ROOT
    / "composeApp"
    / "src"
    / "commonMain"
    / "kotlin"
    / "io"
    / "pylyp"
    / "sample"
    / "composeapp"
    / "di"
    / "AppModules.kt"
)


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except FileNotFoundError:
        print(f"ERROR: missing required file: {path.relative_to(REPO_ROOT)}")
        sys.exit(2)


def expected_feature_modules(feature_name: str) -> list[str]:
    return [
        f":features:{feature_name}:{feature_name}-ui",
        f":features:{feature_name}:{feature_name}-domain",
        f":features:{feature_name}:{feature_name}-data",
        f":features:{feature_name}:{feature_name}-data-network",
    ]


def expected_project_accessors(feature_name: str) -> list[str]:
    return [
        f"projects.features.{feature_name}.{feature_name}Ui",
        f"projects.features.{feature_name}.{feature_name}Domain",
        f"projects.features.{feature_name}.{feature_name}Data",
        f"projects.features.{feature_name}.{feature_name}DataNetwork",
    ]


def expected_module_tokens(feature_name: str) -> list[str]:
    return [
        f"{feature_name}DomainModule",
        f"{feature_name}DataModule",
        f"{feature_name}DataNetworkModule",
    ]


def check_feature(feature_name: str) -> list[str]:
    issues: list[str] = []
    settings_text = read_text(SETTINGS_GRADLE)
    compose_app_text = read_text(COMPOSE_APP_BUILD)
    app_modules_text = read_text(APP_MODULES)

    feature_root = REPO_ROOT / "features" / feature_name
    if not feature_root.exists():
        issues.append(f"Missing feature directory: features/{feature_name}")
    for module_path in expected_feature_modules(feature_name):
        module_dir = REPO_ROOT / module_path.strip(":").replace(":", "/")
        if not module_dir.exists():
            issues.append(f"Missing module directory: {module_dir.relative_to(REPO_ROOT)}")
        if module_path not in settings_text:
            issues.append(f"Missing module include in settings.gradle.kts: {module_path}")

    for accessor in expected_project_accessors(feature_name):
        if accessor not in compose_app_text:
            issues.append(f"Missing composeApp dependency accessor: {accessor}")

    for token in expected_module_tokens(feature_name):
        pattern = re.compile(rf"\b{re.escape(token)}\b")
        if not pattern.search(app_modules_text):
            issues.append(f"Missing AppModules.kt token: {token}")

    return issues


def check_repo() -> list[str]:
    issues: list[str] = []
    for path in (SETTINGS_GRADLE, COMPOSE_APP_BUILD, APP_MODULES):
        if not path.exists():
            issues.append(f"Missing required root file: {path.relative_to(REPO_ROOT)}")
    return issues


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Validate repo module structure and feature root integration."
    )
    parser.add_argument(
        "--feature",
        help="Validate expected modules and root wiring for a specific feature name.",
    )
    args = parser.parse_args()

    issues = check_repo()
    if args.feature:
        issues.extend(check_feature(args.feature))

    if issues:
        print("FAILED")
        for issue in issues:
            print(f"- {issue}")
        return 1

    print("OK")
    if args.feature:
        print(f"Validated feature wiring for: {args.feature}")
    else:
        print("Validated required root files.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
