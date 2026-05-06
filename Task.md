# Home Widget Implementation Tasks

- [x] Phase 1: Review timer drawing/countdown code portability and extract shared visual calculation
  model.
- [x] Phase 1: Add shared timer session snapshot contract and restore helpers.
- [x] Phase 2: Persist shared widget-readable timer session state alongside timer settings.
- [x] Phase 3: Add Android Glance widget UI, receiver, actions, and update scheduling.
- [x] Phase 4: Add iOS WidgetKit extension target files, timeline provider, and App Group data
  sharing.
- [x] Phase 5: Verify sync behavior, battery-aware update policy, and shared logic tests.

# Background Service & Advanced Audio

- [ ] Phase 0: Lock Android-only scope and map existing timer/audio architecture.
- [ ] Phase 1: Extract reusable timer runtime engine from UI store for service/UI drivers.
- [ ] Phase 2: Add Android foreground service with ongoing notification and manifest wiring.
- [ ] Phase 3: Use monotonic timing boundary detection to avoid background drift.
- [ ] Phase 4: Add non-overlapping advanced cue policy (regular tick vs countdown vs
  phase-complete).
- [ ] Phase 5: Extend timer settings model/persistence with countdown toggles.
- [ ] Phase 6: Update bottom-sheet notifications UI and save behavior to restart service.
- [ ] Phase 7: Synchronize service snapshot state with UI digital display.
- [ ] Phase 8: Add tests for audio decisions, persistence, and runtime synchronization.
