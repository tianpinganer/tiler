package Tiler;

import java.io.*;

public class Main {
	
	
	public static void main(String[] args) throws IOException {	
		// 输入参数  包括： 瓦片大小，  zoom 最大最小、  投影 、 经纬度范围
		int tilesize =256;
		int minzoom = 0;
		int maxzoom = 9;
		//String profile = "mercator";
		String profile = "geodetic";
		double []bbox ={-75.0, -55.0, -70, -50};//minx, miny, maxx, maxy
	
		
		int []tminxy = new int[2];
		int []tmaxxy = new int[2];
		int [][]tminmax = new int[maxzoom-minzoom+1][4];
		int i;		
		
		if( profile == "mercator"){
			GlobalMercator mercator = new GlobalMercator( tilesize);
			for( int zoom=minzoom; zoom<= maxzoom; zoom++){
				tminxy = mercator.MetersToTile(bbox[0], bbox[1], zoom);
				tmaxxy = mercator.MetersToTile(bbox[2], bbox[3], zoom);
				
				i= zoom -minzoom;
				tminmax[i][0] = Math.max(0, tminxy[0]);
				tminmax[i][1] = Math.max(0, tminxy[1]);
				tminmax[i][2] = (int) Math.min(Math.pow(2, zoom) -1, tmaxxy[0]);
				tminmax[i][3] = (int) Math.min(Math.pow(2, zoom) -1, tmaxxy[1]);	
			}	
		}else{
			GlobalGeodetic geodetic = new GlobalGeodetic( tilesize);
			for( int zoom=minzoom; zoom<= maxzoom; zoom++){
				tminxy = geodetic.LatLonToTile(bbox[0], bbox[1], zoom);
				tmaxxy = geodetic.LatLonToTile(bbox[2], bbox[3], zoom);
				
				i= zoom -minzoom;
				tminmax[i][0] = Math.max(0, tminxy[0]);
				tminmax[i][1] = Math.max(0, tminxy[1]);
				tminmax[i][2] = (int) Math.min(Math.pow(2, zoom+1) -1, tmaxxy[0]);
				tminmax[i][3] = (int) Math.min(Math.pow(2, zoom) -1, tmaxxy[1]);	
				}
		}
		
		
		for(i=0; i<maxzoom-minzoom+1; i++){
			String out="";
			for(int j=0;j<4;j++){
				out += Integer.toString(tminmax[i][j])+"  ";
			}
			System.out.println(out);
		}
				    
	    
	}
}
