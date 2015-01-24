package applicationLogic;

public class Image {
	private int imageId;
	private String source;

	public Image(int imageId, String source) {
		super();
		this.imageId = imageId;
		this.source = source;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
