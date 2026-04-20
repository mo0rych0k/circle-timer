#!/usr/bin/env python3

from __future__ import annotations

import re
import sys
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[4]
RULES_DIR = REPO_ROOT / ".cursor" / "rules"
SKILLS_DIR = REPO_ROOT / ".cursor" / "skills"
OPTIONAL_RULES = {
    "core-standards.mdc",
}


def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def available_project_skills() -> set[str]:
    return {
        str(path.relative_to(REPO_ROOT)).replace("\\", "/")
        for path in SKILLS_DIR.glob("**/SKILL.md")
    }


def extract_skill_references(rule_text: str) -> list[str]:
    return re.findall(r"`(\.cursor/skills/[^`]+/SKILL\.md)`", rule_text)


def validate_rule(rule_path: Path, allowed_skills: set[str]) -> list[str]:
    issues: list[str] = []
    text = read_text(rule_path)
    rule_name = rule_path.name

    if rule_name not in OPTIONAL_RULES and "## Required Orchestration" not in text:
        issues.append(f"{rule_name}: missing '## Required Orchestration' section")

    for skill_ref in extract_skill_references(text):
        normalized = skill_ref.replace("\\", "/")
        if normalized not in allowed_skills:
            issues.append(
                f"{rule_name}: references unknown project skill '{normalized}'"
            )

    return issues


def main() -> int:
    allowed_skills = available_project_skills()
    issues: list[str] = []

    for rule_path in sorted(RULES_DIR.glob("*.mdc")):
        issues.extend(validate_rule(rule_path, allowed_skills))

    if issues:
        print("FAILED")
        for issue in issues:
            print(f"- {issue}")
        return 1

    print("OK")
    print("Validated rule orchestration coverage and project skill references.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
