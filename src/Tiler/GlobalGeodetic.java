package Tiler;

public class GlobalGeodetic {
	/*TMS Global Geodetic Profile
	---------------------------
	Functions necessary for generation of global tiles in Plate Carre projection,
	EPSG:4326, "unprojected profile".

	Such tiles are compatible with Google Earth (as any other EPSG:4326 rasters)
	and you can overlay the tiles on top of OpenLayers base map.
	
	Pixel and tile coordinates are in TMS notation (origin [0,0] in bottom-left).

	What coordinate conversions do we need for TMS Global Geodetic tiles?

	  Global Geodetic tiles are using geodetic coordinates (latitude,longitude)
	  directly as planar coordinates XY (it is also called Unprojected or Plate
	  Carre). We need only scaling to pixel pyramid and cutting to tiles.
	  Pyramid has on top level two tiles, so it is not square but rectangle.
	  Area [-180,-90,180,90] is scaled to 512x256 pixels.
	  TMS has coordinate origin (for pixels and tiles) in bottom-left corner.
	  Rasters are in EPSG:4326 and therefore are compatible with Google Earth.

	     LatLon      <->      Pixels      <->     Tiles     

	 WGS84 coordinates   Pixels in pyramid  Tiles in pyramid
	     lat/lon         XY pixels Z zoom      XYZ from TMS 
	    EPSG:4326                                           
	     .----.                ----                         
	    /      \     <->    /--------/    <->      TMS      
	    \      /         /--------------/                   
	     -----        /--------------------/                
	   WMS, KML    Web Clients, Google Earth  TileMapService
	*/
	int tileSize=256;
	
	public GlobalGeodetic(int tileSize){
		this.tileSize = tileSize;
	}
	
	public double Resoulution(int zoom){
		return 180.0/this.tileSize/Math.pow(2, zoom);
	}
	
	
	public int[] LatLonToPiexls(double lat,double lon, int zoom){
		//"Converts lat/lon to pixel coordinates in given zoom of the EPSG:4326 pyramid"
		//pixel[1]=px,pixel[2]=py;
		int[] pixel = new int[2];
		double res = this.Resoulution(zoom);
		pixel[0]= (int)((180+lat)/res);
		pixel[1]=(int)((90+lon)/res);
		return pixel;
	}
	
	
	public int[] PixelsToTile(int px, int py){
		//"Returns coordinates of the tile covering region in pixel coordinates"
		//tile[0]=tx; tile[1]=ty
		int[] tile =new int[2];
		tile[0] = (int)(Math.ceil(px/(float)this.tileSize) - 1);
		tile[1] = (int)(Math.ceil(py/(float)this.tileSize) - 1);
		return tile;
	}
	
	
	public int[] LatLonToTile(double lat, double lon, int zoom){
		//"Returns the tile for zoom which covers given lat/lon coordinates"
		int[] pixel = new int[2];
		pixel = this.LatLonToPiexls(lat, lon, zoom);
		return this.PixelsToTile(pixel[0], pixel[1]);
	}
	
	
	public double[] TileBounds(int tx, int ty, int zoom){
		//"Returns bounds of the given tile"
		//tileBounds[tx ,ty, tx+1, ty+1]
		double[] tileBounds = new double[4];
		double res = this.Resoulution(zoom);
		tileBounds[0] = tx*this.tileSize*res-180;
		tileBounds[1] = ty*this.tileSize*res-90;
		tileBounds[2] = (tx+1)*this.tileSize*res-180;
		tileBounds[3] = (ty+1)*this.tileSize*res-90;
		return tileBounds;
	}
	
	
	public double[] TileLatLonBounds(int tx, int ty, int zoom){
		//"Returns bounds of the given tile in the SWNE form"
		double[] SWNE = new double[4];
		double res = this.Resoulution(zoom);
		SWNE[0] = ty*this.tileSize*res-90;
		SWNE[1] = tx*this.tileSize*res-180;
		SWNE[2] = (ty+1)*this.tileSize*res-90;
		SWNE[3] = (tx+1)*this.tileSize*res-180;
		return SWNE;
	}
	
	
	public int ZoomForPixelSize(int pixelSize){
		int zoom;
		for(zoom=0;zoom<32;zoom++){
			if (pixelSize>(int) this.Resoulution(zoom)){
				if (zoom!=0)
					return zoom-1;
				else
					return 0;
			}
		}
		return 0;
	}
	
	
	
}
