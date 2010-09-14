// Copyright (C) 2010 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package uk.org.toot.audio.reverb;

/**
 * An implementation of the network diagram from 
 * http://spinsemi.com/knowledge_base/effects.html#Reverberation
 * This is based on Keith Barr's work for later Alesis hardware reverberators.
 * RIP Keith Barr 1949 - 2010
 * @author st
 */
public class BarrProcess extends AbstractReverbProcess
{
	private Variables vars;
	private float zm1 = 0f;
	private Block block1, block2, block3, block4;
    private Filter damp1, damp2;
	
	private Delay ipd;
	private Filter bw;
	private Diffuser id1a, id1b, id2a, id2b;
	
	private int preDelaySamples;
	private float bandwidth;
	private float inputDiffusion1, inputDiffusion2;
	private float damping, decay;
	private float decayDiffusion1, decayDiffusion2;
	
	public BarrProcess(Variables vars) {
		this.vars = vars;
		ipd = new Delay(1+vars.getMaxPreDelaySamples());
		bw = new Filter();
        int[][] sizes = vars.getSizes();
        // input diffusion
        int[] sz = sizes[4];
		id1a = new Diffuser(sz[0]);
		id1b = new Diffuser(sz[1]);
		id2a = new Diffuser(sz[2]);
		id2b = new Diffuser(sz[3]);
        // the 4 blocks form a tank for the reverb tail
        int[][] tapsLeft = vars.getLeftTaps();
        int[][] tapsRight = vars.getRightTaps();
        block1 = new Block(sizes[0], tapsLeft[0], tapsRight[0]);
        block2 = new Block(sizes[1], tapsLeft[1], tapsRight[1]);
        block3 = new Block(sizes[2], tapsLeft[2], tapsRight[2]);
        block4 = new Block(sizes[3], tapsLeft[3], tapsRight[3]);
        damp1 = new Filter();
        damp2 = new Filter();
	}
	
	protected void cacheVariables() {
		preDelaySamples = vars.getPreDelaySamples();
		bandwidth = vars.getBandwidth();
		inputDiffusion1 = vars.getInputDiffusion1();
		inputDiffusion2 = vars.getInputDiffusion2();
		damping = vars.getDamping();
		decay = vars.getDecay();
		decayDiffusion1 = vars.getDecayDiffusion1();
		decayDiffusion2 = vars.getDecayDiffusion2();
	}
	
    // could feed left into 1, right into 3 but input diffusion would need to be stereo!
	protected void reverb(float left, float right) {
		float sample = 0.3f * idiffuse(left + right);
		zm1 = 
            block4.tick(sample + 
                damp2.filter(
                    block3.tick(sample + 
                        block2.tick(sample +
                            damp1.filter(
                                block1.tick(sample + zm1), 
                            damping))), 
                damping));
	}
	
    protected float left() { 
        return block1.left() + 
               block2.left() +
               block3.left() +
               block4.left(); 
    }
    
    protected float right() { 
        return block1.right() + 
               block2.right() +
               block3.right() +
               block4.right(); 
    }
    
	private float idiffuse(float sample) {
		// pre delay
		ipd.delay(sample);
		// bandwidth, input diffusion 1 x 2, input diffusion 2 x 2
		return id2b.diffuse(
				id2a.diffuse(
					id1b.diffuse(
						id1a.diffuse(
							bw.filter(
								ipd.tap(preDelaySamples), 
                                1-bandwidth), 
							inputDiffusion1), 
						inputDiffusion1), 
					inputDiffusion2), 
				inputDiffusion2);
	}
	
	private class Block
	{
		private final Diffuser dif1, dif2;
		private final Delay del;
        private final int[] tapsLeft, tapsRight;
		
		public Block(int[] sz, int[] left, int[] right) {
            tapsLeft = left;
            tapsRight = right;
			dif1 = new Diffuser(sz[0]);
			dif2 = new Diffuser(sz[1]);
            del = new Delay(sz[2]);
		}
		
		public float tick(float sample) {
            // diffuse, diffuse, delay, decay
			return decay * del.delay(
                    dif2.diffuse(
                        dif1.diffuse(sample, 
                            -decayDiffusion1), 
                        decayDiffusion2));
		}
		
        public float left() {
            return del.tap(tapsLeft[0]) + del.tap(tapsLeft[1]);
        }
        
        public float right() {
            return del.tap(tapsRight[0]) + del.tap(tapsRight[1]);
        }
	}
    
    public interface Variables
    {
        boolean isBypassed();
        int getPreDelaySamples();
        float getBandwidth();       // 0..1
        float getInputDiffusion1(); // 0..1
        float getInputDiffusion2(); // 0..1
        float getDecayDiffusion1(); // 0..1
        float getDecayDiffusion2(); // 0..1
        float getDamping();         // 0..1
        float getDecay();           // 0..1
        // following methods are called once at startup
        int getMaxPreDelaySamples();
        int[][] getSizes();         // [block][3|4] (dif1, dif2, del)|(id1, id2, id3, id4)
        int[][] getLeftTaps();      // [block][2]
        int[][] getRightTaps();     // [block][2]
    }

}
