package com.flipkart.gjex.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Duration implements Comparable<Duration> {
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)\\s*(\\S+)");
    private static final Map<String, TimeUnit> SUFFIXES;
    private final long count;
    private final TimeUnit unit;

    public static Duration nanoseconds(long count) {
        return new Duration(count, TimeUnit.NANOSECONDS);
    }

    public static Duration microseconds(long count) {
        return new Duration(count, TimeUnit.MICROSECONDS);
    }

    public static Duration milliseconds(long count) {
        return new Duration(count, TimeUnit.MILLISECONDS);
    }

    public static Duration seconds(long count) {
        return new Duration(count, TimeUnit.SECONDS);
    }

    public static Duration minutes(long count) {
        return new Duration(count, TimeUnit.MINUTES);
    }

    public static Duration hours(long count) {
        return new Duration(count, TimeUnit.HOURS);
    }

    public static Duration days(long count) {
        return new Duration(count, TimeUnit.DAYS);
    }

    @JsonCreator
    public static Duration parse(String duration) {
        Matcher matcher = DURATION_PATTERN.matcher(duration);
        Preconditions.checkArgument(matcher.matches(), "Invalid duration: " + duration);
        long count = Long.parseLong(matcher.group(1));
        TimeUnit unit = (TimeUnit)SUFFIXES.get(matcher.group(2));
        if (unit == null) {
            throw new IllegalArgumentException("Invalid duration: " + duration + ". Wrong time unit");
        } else {
            return new Duration(count, unit);
        }
    }

    private Duration(long count, TimeUnit unit) {
        this.count = count;
        this.unit = (TimeUnit) Objects.requireNonNull(unit);
    }

    public long getQuantity() {
        return this.count;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }

    public long toNanoseconds() {
        return TimeUnit.NANOSECONDS.convert(this.count, this.unit);
    }

    public long toMicroseconds() {
        return TimeUnit.MICROSECONDS.convert(this.count, this.unit);
    }

    public long toMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.count, this.unit);
    }

    public long toSeconds() {
        return TimeUnit.SECONDS.convert(this.count, this.unit);
    }

    public long toMinutes() {
        return TimeUnit.MINUTES.convert(this.count, this.unit);
    }

    public long toHours() {
        return TimeUnit.HOURS.convert(this.count, this.unit);
    }

    public long toDays() {
        return TimeUnit.DAYS.convert(this.count, this.unit);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Duration duration = (Duration)obj;
            return this.count == duration.count && this.unit == duration.unit;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return 31 * (int)(this.count ^ this.count >>> 32) + this.unit.hashCode();
    }

    @JsonValue
    public String toString() {
        String units = this.unit.toString().toLowerCase(Locale.ENGLISH);
        if (this.count == 1L) {
            units = units.substring(0, units.length() - 1);
        }

        return Long.toString(this.count) + ' ' + units;
    }

    public int compareTo(Duration other) {
        return this.unit == other.unit ? Long.compare(this.count, other.count) : Long.compare(this.toNanoseconds(), other.toNanoseconds());
    }

    static {
        SUFFIXES = (new ImmutableMap.Builder()).put("ns", TimeUnit.NANOSECONDS).put("nanosecond", TimeUnit.NANOSECONDS).put("nanoseconds", TimeUnit.NANOSECONDS).put("us", TimeUnit.MICROSECONDS).put("microsecond", TimeUnit.MICROSECONDS).put("microseconds", TimeUnit.MICROSECONDS).put("ms", TimeUnit.MILLISECONDS).put("millisecond", TimeUnit.MILLISECONDS).put("milliseconds", TimeUnit.MILLISECONDS).put("s", TimeUnit.SECONDS).put("second", TimeUnit.SECONDS).put("seconds", TimeUnit.SECONDS).put("m", TimeUnit.MINUTES).put("minute", TimeUnit.MINUTES).put("minutes", TimeUnit.MINUTES).put("h", TimeUnit.HOURS).put("hour", TimeUnit.HOURS).put("hours", TimeUnit.HOURS).put("d", TimeUnit.DAYS).put("day", TimeUnit.DAYS).put("days", TimeUnit.DAYS).build();
    }
}

