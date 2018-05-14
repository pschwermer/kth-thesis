package com.benchmarks.patrik.benchmarks.kotlinIdiomatic

import java.lang.Math.sqrt

/* The Computer Language Benchmarks Game
   http://benchmarksgame.alioth.debian.org/
   contributed by Mark C. Lewis
   modified slightly by Chad Whipkey
   implemented in Kotlin by Patrik Schwermer
*/

private const val PI = 3.141592653589793
private const val SOLAR_MASS = 4 * PI * PI
private const val DAYS_PER_YEAR = 365.24

data class Body(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0,
                var vx: Double = 0.0, var vy: Double = 0.0, var vz: Double = 0.0,
                var mass: Double = 0.0)

class BodySystem {
    //Array of celestial bodies: Sun, Jupiter, Saturn, Uranus and Neptune
    private var bodies: Array<Body> = arrayOf(
            Body(mass = SOLAR_MASS),
            Body(4.84143144246472090e+00,
                    -1.16032004402742839e+00,
                    -1.03622044471123109e-01,
                    1.66007664274403694e-03 * DAYS_PER_YEAR,
                    7.69901118419740425e-03 * DAYS_PER_YEAR,
                    -6.90460016972063023e-05 * DAYS_PER_YEAR,
                    9.54791938424326609e-04 * SOLAR_MASS),
            Body(8.34336671824457987e+00,
                    4.12479856412430479e+00,
                    -4.03523417114321381e-01,
                    -2.76742510726862411e-03 * DAYS_PER_YEAR,
                    4.99852801234917238e-03 * DAYS_PER_YEAR,
                    2.30417297573763929e-05 * DAYS_PER_YEAR,
                    2.85885980666130812e-04 * SOLAR_MASS),
            Body(1.28943695621391310e+01,
                    -1.51111514016986312e+01,
                    -2.23307578892655734e-01,
                    2.96460137564761618e-03 * DAYS_PER_YEAR,
                    2.37847173959480950e-03 * DAYS_PER_YEAR,
                    -2.96589568540237556e-05 * DAYS_PER_YEAR,
                    4.36624404335156298e-05 * SOLAR_MASS),
            Body(1.53796971148509165e+01,
                    -2.59193146099879641e+01,
                    1.79258772950371181e-01,
                    2.68067772490389322e-03 * DAYS_PER_YEAR,
                    1.62824170038242295e-03 * DAYS_PER_YEAR,
                    -9.51592254519715870e-05 * DAYS_PER_YEAR,
                    5.15138902046611451e-05 * SOLAR_MASS)
    )

    //Offset the momentum of the sun
    init {
        var px = 0.0
        var py = 0.0
        var pz = 0.0

        bodies.forEach { body ->
            px += body.vx * body.mass
            py += body.vy * body.mass
            pz += body.vz * body.mass
        }

        bodies[0].vx = -px / SOLAR_MASS
        bodies[0].vy = -py / SOLAR_MASS
        bodies[0].vz = -pz / SOLAR_MASS
    }

    fun advance(dt: Double) {
        for (i in bodies.indices) {
            val currBody = bodies[i]
            for (j in i + 1 until bodies.size) {
                val dx = currBody.x - bodies[j].x
                val dy = currBody.y - bodies[j].y
                val dz = currBody.z - bodies[j].z

                val dSquared = dx * dx + dy * dy + dz * dz
                val mag = dt / (dSquared * sqrt(dSquared))

                currBody.vx -= dx * bodies[j].mass * mag
                currBody.vy -= dy * bodies[j].mass * mag
                currBody.vz -= dz * bodies[j].mass * mag

                bodies[j].vx += dx * currBody.mass * mag
                bodies[j].vy += dy * currBody.mass * mag
                bodies[j].vz += dz * currBody.mass * mag
            }
        }

        bodies.forEach { body ->
            body.x += dt * body.vx
            body.y += dt * body.vy
            body.z += dt * body.vz
        }
    }

    fun energy(): Double {
        var dx: Double
        var dy: Double
        var dz: Double
        var e = 0.0

        for (i in bodies.indices) {
            val (x, y, z, vx, vy, vz, mass) = bodies[i]
            e += 0.5 * mass * (vx * vx + vy * vy + vz * vz)

            for (j in i + 1 until bodies.size) {
                val (x1, y1, z1, _, _, _, mass1) = bodies[j]
                dx = x - x1
                dy = y - y1
                dz = z - z1

                e -= mass * mass1 / sqrt(dx * dx + dy * dy + dz * dz)
            }
        }
        return e
    }
}

class nbody {
    fun runBenchmark(args: Array<String>) {
        val n: Int = args[0].toInt()
        val system = BodySystem()
        println("%.9f".format(system.energy()))
        repeat(times = n) {
            system.advance(0.01)
        }
        println("%.9f".format(system.energy()))
    }
}