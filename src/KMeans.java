/**
 * A k-means clustering algorithm implementation.
 * 
 */

// John Becker, CS540 

import java.util.*;
import java.lang.Math;

public class KMeans {
	public KMeansResult cluster(double[][] centroids, double[][] instances, double threshold) {
		/* ... YOUR CODE GOES HERE ... */
		KMeansResult results = new KMeansResult();
		int[] clusterAssignments = new int[instances.length];
		ArrayList<Double> distortionIterations = new ArrayList<Double>();
		double[] currDistance = new double[centroids.length]; // Used to hold the distance 
															  // to all centroids for a certian
															  // instance.
		
		double[][] tmpCentroids = centroids; // Hold the changed centroid values
		
		boolean orphans = false; // Indicates if there is orphans
		boolean done = false; // Used to indicate when threshold is met
		
		double tmpDistance = 0; 
		double min = 0;
		
		int clusterAss = 0;
		int numIterations = 0;
		
		while(!done){
			// Assign instances to centroid
			orphans = false;
			//Loop through all instances
			for(int i = 0; i < instances.length; i++){
				// For each instances loop through centriods and calc distance
				for(int j = 0; j < currDistance.length; j++){
					// Go through all features and calc distance
					tmpDistance = 0;
					for(int k = 0; k < instances[0].length; k++){
						tmpDistance = tmpDistance + Math.pow((tmpCentroids[j][k] - instances[i][k]), 2); 
					}
					currDistance[j] = Math.sqrt(tmpDistance);
				}
				min = currDistance[0];
				clusterAss = 0;
				for(int l = 1; l < currDistance.length; l++){
					if(min > currDistance[l]){
							min = currDistance[l];
							clusterAss = l;
					}
				}
				clusterAssignments[i] = clusterAss;
			}
			// check for orphans
			// loop through centroids
			for(int i = 0; i < tmpCentroids.length; i++){
				//If centroid id is not in clusterAssignment handle orphan
				boolean clusterAssigned = false;
				// Checks if clusterAssignments contains the ID of the current centroid
				for(int m = 0; m < clusterAssignments.length; m++){
					if(clusterAssignments[m] == i){
						clusterAssigned = true;
						m = clusterAssignments.length;
					}
				}
				if(!clusterAssigned){
					int furthestInstance = 0; // Holds id of instance
					double maxDis = 0;
					// Loop through all the instances
					for(int j = 0; j < instances.length; j ++){
						double tmpMaxDis = 0;
						// Loop through all instances features
						for(int k = 0; k < instances[0].length; k++){
							tmpMaxDis = tmpMaxDis + Math.pow((tmpCentroids[clusterAssignments[j]][k] 
									- instances[j][k]), 2);
						}
						if(maxDis < tmpMaxDis){
							maxDis = tmpMaxDis;
							furthestInstance = j;
						}
					}
					// set all fields of orphan centroid to furthest instance 
					for(int l = 0; l < instances[0].length; l++){
						tmpCentroids[i][l] = instances[furthestInstance][l];
					}
					orphans = true;
					i = tmpCentroids.length;
				}
			}
			//Update tmpCentroids coordinates and calculate distortions
			if(!orphans){
				// loop through the current centroids
				for(int i = 0; i < tmpCentroids.length; i++){
					// For each centroid loop through its features
					for(int j = 0; j < tmpCentroids[0].length; j++){
						double newFeatureDistance = 0;
						int clusterInstancesCount = 0;
						// At each centroids feature loop through all its instances and update its
						// feature value
						for(int k = 0; k < instances.length; k++){
							if(clusterAssignments[k] == i){
								newFeatureDistance = newFeatureDistance + instances[k][j];
								clusterInstancesCount++;
							}
						}
						tmpCentroids[i][j] = (newFeatureDistance / clusterInstancesCount);
					}
				}
	
				// Calculate Distortions
				double totalDistortion = 0;
				// Loop through all the instances features
				for(int i = 0; i < instances.length; i++){
					double instanceDistortion = 0;
					// At each instances loop through all its features and use the centroid it belongs 
					// to in the distortion calculation
					for(int j = 0; j < instances[0].length; j++){
							instanceDistortion = instanceDistortion + Math.pow((instances[i][j]
									- tmpCentroids[clusterAssignments[i]][j]), 2);
					}
					totalDistortion = totalDistortion + instanceDistortion;
				}
				// Add distortion to list and increment the number of iterations	 
				distortionIterations.add(totalDistortion);
				numIterations++;
				
				//Need to do at least one iterations of kmeans to test if below threshold
				if(numIterations > 1){
					if(Math.abs((distortionIterations.get(numIterations - 1) - 
							distortionIterations.get(numIterations - 2)) / 
							distortionIterations.get(numIterations - 2) ) < threshold){
						done = true;
					}			
				}
			}
		}
		
		double[] distortion = new double[distortionIterations.size()];
		Iterator<Double> itr = distortionIterations.iterator();
		int i = 0;
		
		while(itr.hasNext()){
			distortion[i] = itr.next().doubleValue();
			i++;
		}
		
		// Set result fields
		results.centroids = tmpCentroids;
		results.clusterAssignment = clusterAssignments;
		results.distortionIterations = distortion;
		
		
		return results;
	}
}
