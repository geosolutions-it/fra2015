/*
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.fra2015.importer;

/**
 * @author DamianoG
 *
 */
public enum ReservedHeaderNames {
    YEAR ("Year"),
    COUNTRY ("Country"),
    DESK_STUDY ("Desk study");
    
    private final String name; // in meters
    
    ReservedHeaderNames(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() { 
        return name; 
    }
    
    public static boolean isReserved(String inName){
        for(ReservedHeaderNames name : values()){
            if(name.toString().equals(inName)){
                return true;
            }
        }
        return false;
    }
}
