/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.collections.graphdb.impl;

import org.neo4j.collections.graphdb.GraphDatabaseService;
import org.neo4j.collections.graphdb.Node;
import org.neo4j.collections.graphdb.Property;
import org.neo4j.collections.graphdb.PropertyContainer;
import org.neo4j.collections.graphdb.PropertyType;
import org.neo4j.collections.graphdb.Relationship;

public class PropertyImpl<T> extends ElementImpl implements Property<T>{

	public final static String PROPERTYCONTAINER_ID = "org.neo4j.collections.graphdb.propertycontainer_id";
	public final static String PROPERTYCONTAINER_TYPE = "org.neo4j.collections.graphdb.propertycontainer_type";
	public final static String PROPERTY_NAME = "org.neo4j.collections.graphdb.property_name";

	
	public enum PropertyContainerType{
		NODE, RELATIONSHIP
	}
	
	private final PropertyContainer pc;
	private final PropertyType<T> propertyType;
	private final GraphDatabaseService graphDb;
	private org.neo4j.collections.graphdb.Node node;

	PropertyImpl(GraphDatabaseService graphDb, PropertyContainer pc, PropertyType<T> propertyType){
		this.pc = pc;
		this.propertyType = propertyType;
		this.graphDb = graphDb;
	}
	
	public long getId(){
		return getNode().getId();
	}
	
	@Override
	public T getValue() {
		return pc.getPropertyValue(propertyType);
	}

	@Override
	public PropertyType<T> getPropertyType() {
		return propertyType;
	}

	@Override
	public org.neo4j.graphdb.PropertyContainer getPropertyContainer() {
		return getNode();
	}

	@Override
	public GraphDatabaseService getGraphDatabase() {
		return graphDb;
	}

	@Override
	public Iterable<PropertyType<?>> getPropertyTypes() {
		return null;
	}

	@Override
	public org.neo4j.graphdb.Node getNode() {
		if(node != null){
			return node.getNode();
		}else{
			if(pc.getPropertyContainer().hasProperty(propertyType.getName()+".node_id")){
				return getGraphDatabase().getNodeById((Long)pc.getPropertyContainer().getProperty(propertyType.getName()+".node_id")).getNode();
			}else{
				Node n = graphDb.createNode();
				n.setProperty(PROPERTYCONTAINER_ID, pc.getId());
				n.setProperty(PROPERTY_NAME, propertyType.getName());
				if(pc instanceof Relationship){
					n.setProperty(PROPERTYCONTAINER_TYPE, PropertyContainerType.RELATIONSHIP.name());
				}else{
					n.setProperty(PROPERTYCONTAINER_TYPE, PropertyContainerType.NODE.name());
				}
				pc.getPropertyContainer().setProperty(propertyType.getName()+".node_id", n.getId());
				return n.getNode();
			}
		}
	}

	@Override
	public PropertyContainer getPropertyContainerExt() {
		return new NodeImpl(getNode());
	}

}
