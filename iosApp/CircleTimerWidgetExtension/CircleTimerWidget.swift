import WidgetKit
import SwiftUI

private enum WidgetSharedKeys {
    static let appGroupId = "group.com.circle.timer"
    static let totalDuration = "timer.totalDurationSeconds"
    static let breakDuration = "timer.breakDurationSeconds"
    static let isRunning = "timer.widget.snapshot.isRunning"
    static let phase = "timer.widget.snapshot.phase"
    static let phaseStartedAt = "timer.widget.snapshot.phaseStartedAtEpochMillis"
}

private struct TimerEntry: TimelineEntry {
    let date: Date
    let remainingSeconds: Int
    let progress: Double
    let isRunning: Bool
}

private struct CircleTimerProvider: TimelineProvider {
    func placeholder(in context: Context) -> TimerEntry {
        TimerEntry(date: .now, remainingSeconds: 60, progress: 0.0, isRunning: false)
    }

    func getSnapshot(in context: Context, completion: @escaping (TimerEntry) -> Void) {
        completion(loadEntry())
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<TimerEntry>) -> Void) {
        let current = loadEntry()
        let refresh = current.isRunning ? Calendar.current.date(byAdding: .second, value: 1, to: .now)! :
            Calendar.current.date(byAdding: .minute, value: 15, to: .now)!
        completion(Timeline(entries: [current], policy: .after(refresh)))
    }

    private func loadEntry() -> TimerEntry {
        let defaults = UserDefaults(suiteName: WidgetSharedKeys.appGroupId) ?? .standard
        let totalDuration = defaults.object(forKey: WidgetSharedKeys.totalDuration) as? Int ?? 60
        let breakDuration = defaults.object(forKey: WidgetSharedKeys.breakDuration) as? Int ?? 0
        let running = defaults.bool(forKey: WidgetSharedKeys.isRunning)
        guard running else {
            return TimerEntry(date: .now, remainingSeconds: totalDuration, progress: 0.0, isRunning: false)
        }
        let phase = defaults.string(forKey: WidgetSharedKeys.phase) ?? "Active"
        let started = defaults.object(forKey: WidgetSharedKeys.phaseStartedAt) as? Int64 ?? 0
        let elapsed = max(0, Int(Date().timeIntervalSince1970 * 1000) - Int(started))
        let phaseDuration = phase == "Break" ? breakDuration : totalDuration
        let elapsedSec = elapsed / 1000
        let remaining = max(0, phaseDuration - elapsedSec)
        let progress = phaseDuration > 0 ? min(1.0, Double(elapsedSec) / Double(phaseDuration)) : 0.0
        return TimerEntry(date: .now, remainingSeconds: remaining, progress: progress, isRunning: true)
    }
}

private struct CircleTimerWidgetView: View {
    let entry: TimerEntry

    var body: some View {
        Link(destination: URL(string: "circletimer://timer")!) {
            ZStack {
                Circle()
                    .stroke(Color.white.opacity(0.15), lineWidth: 12)
                Circle()
                    .trim(from: 0, to: entry.progress)
                    .stroke(Color.cyan, style: StrokeStyle(lineWidth: 12, lineCap: .round))
                    .rotationEffect(.degrees(-90))
                VStack(spacing: 4) {
                    Text("\(entry.remainingSeconds)")
                        .font(.system(size: 32, weight: .bold))
                        .foregroundStyle(.white)
                    Text(entry.isRunning ? "Stop" : "Play")
                        .font(.caption)
                        .foregroundStyle(.white.opacity(0.7))
                }
            }
            .padding(16)
            .containerBackground(.black, for: .widget)
        }
    }
}

struct CircleTimerWidget: Widget {
    let kind: String = "CircleTimerWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: CircleTimerProvider()) { entry in
            CircleTimerWidgetView(entry: entry)
        }
        .configurationDisplayName("Circle Timer")
        .description("Shows timer progress and opens the app.")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}
