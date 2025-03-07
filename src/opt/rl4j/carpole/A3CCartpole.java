/*******************************************************************************
 * Copyright (c) 2015-2019 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package opt.rl4j.carpole;

import opt.rl4j.CMDHelper;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense;
import org.deeplearning4j.rl4j.mdp.gym.GymEnv;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.space.Box;
import org.deeplearning4j.rl4j.util.DataManager;
import org.nd4j.linalg.learning.config.Adam;
import opt.rl4j.CMDHelper;

import java.io.IOException;

/**
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 8/18/16.
 * <p>
 * main example for A3C on cartpole
 */
public class A3CCartpole {

    private static A3CDiscrete.A3CConfiguration CARTPOLE_A3C =
            new A3CDiscrete.A3CConfiguration(
                    123,            //Random seed
                    200,            //Max step By epoch
                    500000,         //Max step
                    16,              //Number of threads
                    5,              //t_max
                    10,             //num step noop warmup
                    0.01,           //reward scaling
                    0.99,           //gamma
                    10.0           //td-error clipping
            );


    private static final ActorCriticFactorySeparateStdDense.Configuration CARTPOLE_NET_A3C = ActorCriticFactorySeparateStdDense.Configuration
            .builder().updater(new Adam(1e-2)).l2(0).numHiddenNodes(16).numLayer(3).build();

    public static void main(String[] args) throws IOException {
        // 启动http服务
        CMDHelper.startHttpServer();
        A3CcartPole();
    }

    public static void A3CcartPole() throws IOException {

        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager(true);

        //define the mdp from gym (name, render)
        GymEnv mdp = null;
        try {
            mdp = new GymEnv("CartPole-v0", false, false);
        } catch (RuntimeException e) {
            System.out.print("To run this example, download and start the gym-http-api repo found at https://github.com/openai/gym-http-api.");
        }

        //define the training
        A3CDiscreteDense<Box> a3c = new A3CDiscreteDense<Box>(mdp, CARTPOLE_NET_A3C, CARTPOLE_A3C, manager);

        //start the training
        a3c.train();

        ACPolicy<Box> pol = a3c.getPolicy();

        pol.save("data/val1/", "data/pol1");

        //close the mdp (http connection)
        mdp.close();

        //reload the policy, will be equal to "pol", but without the randomness
        ACPolicy<Box> pol2 = ACPolicy.load("data/val1/", "data/pol1");
    }
}
