package com.github.vincentvangestel.osmdot.pruner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.LengthData;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.geom.TableGraph;
import com.google.common.collect.Lists;


public class RoundAboutPrunerTest {
  static final double DELTA = 0.0001;

  Graph<MultiAttributeData> graph;
  static final MultiAttributeData data = MultiAttributeData.builder().setMaxSpeed(10).build();
  static final RoundAboutPruner pruner = new RoundAboutPruner();

  static final Connection<LengthData> DUMMY = Connection.create(
    new Point(0, 0), new Point(1, 1));

  @Before
  public void setUp() {
    graph = new TableGraph<>();
  }

  @Test
  public void pruneCycleTest() {
	  Point A,B,C,D,E,F;
	  Point O,I,N;
	  Point Center;
	  
	  A = new Point(10,10);
	  B = new Point(5,15);
	  C = new Point(10,20);
	  D = new Point(15,20);
	  E = new Point(20,15);
	  F = new Point(15,10);
	  
	  Center = Point.center(Lists.newArrayList(A, B, C, D, E, F));
	  
	  O = new Point(300,300);
	  I = new Point(300,150);
	  N = new Point(10,300);
	  
	  
	  graph.addConnection(A, B, data);
	  graph.addConnection(B, C, data);
	  graph.addConnection(C, D, data);
	  graph.addConnection(D, E, data);
	  graph.addConnection(E, F, data);
	  graph.addConnection(F, A, data); // The cycle
	  
	  graph.addConnection(D, O, data); // Outgoing
	  graph.addConnection(I, E, data); // Incoming
	  graph.addConnection(C, N, data);
	  graph.addConnection(N, C, data); // Bi-directional
	  
	  graph = pruner.prune(graph);
	  
	  assertEquals(4,graph.getNumberOfNodes());
	  assertTrue(graph.hasConnection(Center, O)); // Outgoing
	  assertTrue(graph.hasConnection(I, Center)); // Incoming
	  assertTrue(graph.hasConnection(N, Center));
	  assertTrue(graph.hasConnection(Center, N)); // Bi-directional
  }
  
  @Test
  public void pruneMultipleContainedCycleTest() {
	  Point A,B,C,D,E,F;
	  Point O,I,N,N2;
	  Point Center;
	  
	  A = new Point(10,10);
	  B = new Point(5,15);
	  C = new Point(10,20);
	  D = new Point(15,20);
	  E = new Point(20,15);
	  F = new Point(15,10);
	  N = new Point(10, 30);
	  
	  Center = Point.center(Lists.newArrayList(A, B, C, D, E, F, N));
	  
	  O = new Point(300,300);
	  I = new Point(300,150);
	  N2 = new Point(10,300);
	  
	  
	  graph.addConnection(A, B, data);
	  graph.addConnection(B, C, data);
	  graph.addConnection(C, D, data);
	  graph.addConnection(D, E, data);
	  graph.addConnection(E, F, data);
	  graph.addConnection(F, A, data);
	  graph.addConnection(C, N, data);
	  graph.addConnection(N, C, data);// The cycle
	  
	  graph.addConnection(D, O, data); // Outgoing
	  graph.addConnection(I, E, data); // Incoming
	  graph.addConnection(N, N2, data);
	  graph.addConnection(N2, N, data); // Bi-directional
	  
	  graph = pruner.prune(graph);
	  
	  assertEquals(4,graph.getNumberOfNodes());
	  assertTrue(graph.hasConnection(Center, O)); // Outgoing
	  assertTrue(graph.hasConnection(I, Center)); // Incoming
	  assertTrue(graph.hasConnection(N2, Center));
	  assertTrue(graph.hasConnection(Center, N2)); // Bi-directional
  }
  
  @Test
  public void pruneMultipleTouchingCycleTest() {
	  Point A1,B1,C1,D1,E,F1;
	  Point A2,B2,C2,D2,F2;
	  Point Center1;
	  Point Center2;
	  
	  A1 = new Point(10,10);
	  B1 = new Point(0,20);
	  C1 = new Point(10,30);
	  D1 = new Point(20,30);
	  E = new Point(30,20);
	  F1 = new Point(20,10);
	  
	  A2 = new Point(50,10);
	  B2 = new Point(60,20);
	  C2 = new Point(50,30);
	  D2 = new Point(40,30);
	  F2 = new Point(40,10);
	  
	  Center1 = Point.center(Lists.newArrayList(A1, B1, C1, D1, E, F1));
	  Center2 = Point.center(Lists.newArrayList(A2, B2, C2, D2, E, F2));
  
	  
	  graph.addConnection(A1, B1, data);
	  graph.addConnection(B1, C1, data);
	  graph.addConnection(C1, D1, data);
	  graph.addConnection(D1, E, data);
	  graph.addConnection(E, F1, data);
	  graph.addConnection(F1, A1, data); // Cycle 1
	  
	  graph.addConnection(A2, B2, data);
	  graph.addConnection(B2, C2, data);
	  graph.addConnection(C2, D2, data);
	  graph.addConnection(D2, E, data);
	  graph.addConnection(E, F2, data);
	  graph.addConnection(F2, A2, data); // Cycle 2
	  
	  graph = pruner.prune(graph);
	  
	  assertEquals(2,graph.getNumberOfNodes());
	  assertTrue(graph.hasConnection(Center1, Center2));
	  assertTrue(graph.hasConnection(Center2, Center1));
  }

}
