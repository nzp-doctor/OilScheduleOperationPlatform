/*
 * Copyright 1990-2008, Mark Little, University of Newcastle upon Tyne
 * and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 1990-2008,
 */

package opt.javasim;

/**
 * This exception is thrown if the simulation is restarted. Simulation entities
 * are expected to catch this and do any tidy up necessary, prior to the restart
 * of the simulation later.
 *
 * @author marklittle
 */
public class RestartException extends Exception {
    public static final long serialVersionUID = 0xdeadbeef;

    public RestartException() {
        super();
    }

    public RestartException(String s) {
        super(s);
    }
}
