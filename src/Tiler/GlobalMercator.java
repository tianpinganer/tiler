package Tiler;

public class GlobalMercator {
	int tileSize;
	double initialResolution;
	double originShift;
	
	public GlobalMercator(int tileSize){
		this.tileSize = tileSize;
		this.initialResolution = 2 * Math.PI * 6378137 / this.tileSize;
		this.originShift = 2 * Math.PI * 6378137 /2.0;
	}
	
	public double[] LatLonToMeters(double lat, double lon ){
		double []mxy = new double[2];
		mxy[0] = lon * this.originShift / 180.0;
		mxy[1] = Math.log( Math.tan((90 + lat) * Math.PI / 360.0 )) / (Math.PI / 180.0);
		return mxy;
	}
	
	public double[] MetersToLatLon( double mx, double my){
		double []latlon = new double[2];
		latlon[0] = ( my / this.originShift ) * 180.0;
		latlon[1] = ( mx / this.originShift ) * 180.0;
		latlon[0] = 180 / Math.PI * (2 * Math.atan( Math.exp( latlon[0] * Math.PI / 180.0)) - Math.PI / 2.0);
		return latlon;
	}
	
	
	public double[] PixelsToMeters(int px, int py, int zoom){
		double []mxy = new double[2];
		double res = this.Resolution(zoom);
		mxy[0] = px * res - this.originShift;
		mxy[1] = py * res - this.originShift;
		return mxy;
	}
	
	public int[] MetersToPixels(double mx, double my, int zoom){
		int []pxy = new int[2];
		double res = this.Resolution(zoom);
		pxy[0] = (int) (( mx + this.originShift) / res);
		pxy[1] = (int) (( my + this.originShift) / res);
		return pxy;
	}
	
	public int[] PixelsToTile(int px, int py, int zoom){
		int []txy = new int[2];
		txy[0] = (int)( Math.ceil( px / this.tileSize ) -1);
		txy[1] = (int)( Math.ceil( py / this.tileSize ) -1);
		return txy;
	}
	
	/*Move the origin of pixel coordinates to top-left corner*/
	public int[] PixelsToRaster( int px, int py, int zoom){
		int mapSize = this.tileSize << zoom;
		int []pxy = new int[2];
		pxy[0] = px;
		pxy[1] = mapSize;
		return pxy;
	}
	
	public  int[] MetersToTile( double mx, double my, int zoom){
		int []pxy = new int[2];
		pxy = this.MetersToPixels(mx, my, zoom);
		return this.PixelsToTile(pxy[0], pxy[1], zoom);
	}
	
	public double[] TileBounds( int tx, int ty, int zoom){
		double []minxy = new double[2];//minx miny maxx maxy
		double []maxxy = new double[2];
		minxy = this.PixelsToMeters(tx*this.tileSize, ty*this.tileSize, zoom);
		maxxy = this.PixelsToMeters(tx, ty, zoom);
		double []bbox = {minxy[0], minxy[1], maxxy[0], maxxy[1]};
		return bbox;	
	}
	
	public double[] TileLatLonBounds(int tx, int ty, int zoom){
		double []bounds =new double[4];
		bounds = this.TileBounds(tx, ty, zoom);
		double []minlatlon = new double[2];
		double []maxlatlon = new double[2];
		minlatlon = this.MetersToLatLon(bounds[0], bounds[1]);
		maxlatlon = this.MetersToLatLon(bounds[2], bounds[3]);
		double []bbox = { minlatlon[0], minlatlon[1], maxlatlon[0], maxlatlon[1]};
		return bbox;
	}
	
	/*Maximal scaledown zoom of the pyramid closest to the pixelSize*/
	public int ZoomForPixelSize( double pixelSize){
		for(int i=0; i<32; i++){
			if( pixelSize > this.Resolution(i)){
				if(i !=0 ){
					return i-1;
				}else{
					return 0;
				}
			}
		}
		return 0;
	}
	
	/*coordinate origin is moved from bottom-left to top-left corner of the extent*/
	public int[] GoogleTile( int tx, int ty, int zoom){
		int []txy = new int[2];
		txy[0] = tx;
		txy[1] = (int) (Math.pow(2, zoom) -1 -ty);
		return txy;
	}
	
	
	
	public double Resolution(int zoom){
		return this.initialResolution / Math.pow(2, zoom);
	}
	
	
}
