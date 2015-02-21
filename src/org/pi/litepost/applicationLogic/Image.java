package org.pi.litepost.applicationLogic;

/**
 * @author Julia Moos
 *
 */
public class Image {
	private final int imageId;
	private final String source;

	public Image(int imageId, String source) {
		super();
		this.imageId = imageId;
		this.source = source;
	}

	public int getImageId() {
		return imageId;
	}

	public String getSource() {
		return source;
	}
}
