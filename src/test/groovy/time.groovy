import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

Instant i = Instant.parse("2020-02-21T19:33:33Z")
System.out.println(i);

Instant i2 = i.plus(1, ChronoUnit.HOURS)
System.out.println(i2);

Instant i3= i2
10.times {
    i3=i3.plus(1,ChronoUnit.HOURS)
}

