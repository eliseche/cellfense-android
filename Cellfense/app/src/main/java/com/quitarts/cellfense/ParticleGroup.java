package com.quitarts.cellfense;

import java.util.ArrayList;

public class ParticleGroup {
    private ArrayList<Particle> particles; // TODO: particle pool
    private long lastUpdate;

    public ParticleGroup(int nParticles, int x, int y) {
        particles = new ArrayList<Particle>(nParticles);
        
        for (int i = 0; i < nParticles; i++) {
            Particle p = new Particle(x, y, -150.0f + (float)Math.random()*300.0f, -(float)Math.random()*1000.0f, 1500.0f, System.currentTimeMillis());
            particles.add(p);
        }
        
        lastUpdate = System.currentTimeMillis();
    }

    public void recalc() {
        long now = System.currentTimeMillis();
        
        Object[] pArray = particles.toArray();
        for (int i = 0; i < pArray.length; i++) {
            Particle p = (Particle)pArray[i];
            if (p.ttl == 0)
                particles.remove(p);
        }

        for (Particle p : particles) {
            p.live(now - lastUpdate);
        }
        
        lastUpdate = now;
    }

    // TODO: return Particle[] instead
    public Object[] getParticles() {
        return particles.toArray();
    }

    class Particle { // TODO: unify with class Element
        public final int size = 2;
        public float x;
        public float y;
        public float ttl; // in millisecs
        public float bornTime;
        public float actualTime;
        public float vx;
        public float vy;

        public Particle(int x, int y, float  vx, float vy, float ttl, float bornTime) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.ttl = ttl;
            this.bornTime = bornTime;
            this.actualTime = bornTime;
        }

        /**
         * @param dt in milliseconds
         */
        public void live(float dt) {
            float dtSeg = dt/1000.0f;

            vy += dt; // G ~= 1000 cm/seg OR 1 cm/milliseg
            y += vy * dtSeg;
            x += vx * dtSeg;

            actualTime += dt;

            if (ttl > 0)
                ttl -= dt;

            if (ttl < 0)
                ttl = 0;
        }
    }

}

