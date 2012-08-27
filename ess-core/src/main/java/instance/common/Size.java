///**
// * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package instance.common;
//
///**
// * 
// * @author Amir Moulavi
// * @date 2011-03-28
// *
// */
//
//public enum Size {
//	KB(1024L),
//	MB(1048576L),
//	GB(1073741824L),
//	GHertz(1000000000L);
//	
//	private Long size;
//	
//	Size(Long size) {
//		this.size = size;
//	}
//	
//	public Long getSize() {
//		return size;
//	}
//
//	public static String getCPUHertzString(Long size) {
//		return String.valueOf((double)(size/Size.GHertz.getSize())) + " GHz";
//	}
//	
//	public static Long cpuSpeed(double speed) {
//		return (long) (speed * Size.GHertz.getSize());
//	}
//	
//	public static String getSizeString(Long size) {
//		if (size >= Size.GB.getSize()) {
//			double rem = size.doubleValue() / Size.GB.getSize().doubleValue();
//			return rem + " GB";
//		} else if (size >= Size.MB.getSize()) {
//			double rem = size.doubleValue() / Size.MB.getSize().doubleValue();
//			return rem + " MB";
//		} else if (size >= Size.KB.getSize()) {
//			double rem = size.doubleValue() / Size.KB.getSize().doubleValue();
//			return rem + " KB";
//		} else return size + " B";
//	}
//}
