package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	private NYCDao dao;
	private Graph<String,DefaultWeightedEdge>grafo;
	
	
	
	public Model() {
		dao=new NYCDao();
	}public List<String> getAllProvider(){
		return dao.getAllProvider();
	}
	public void creaGrafo(String provider) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo,this.dao.getVertici(provider));
		for(Adiacenza a:this.dao.getArchi(provider)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getC1(), a.getC2(),a.getPeso());
		}
	}
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	public List<Adiacenza> getAdiacenti(String quartiere) {
		List<String>vicini=Graphs.neighborListOf(grafo, quartiere);
		List<Adiacenza>result=new ArrayList<>();
		for(String s:vicini) {
			result.add(new Adiacenza(s,this.grafo.getEdgeWeight(this.grafo.getEdge(quartiere, s))));
		}
		Collections.sort(result);
		
	return result;}
	public List<String> getVertici(){
		return new ArrayList<>(this.grafo.vertexSet());
	}
	
	public boolean grafoCreato() {
		if(this.grafo == null)
			return false;
		else 
			return true;
	}
}
