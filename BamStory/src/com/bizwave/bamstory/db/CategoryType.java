package com.bizwave.bamstory.db;

public enum CategoryType {
	TYPE(){
		public String getLabel(){
			return "업소종류별";
		}
	},
	REGION(){
		public String getLabel(){
			return "지역별";
		}
	},
	PRICE(){
		public String getLabel(){
			return "가격대별";
		}
	},
	LEVEL(){
		public String getLabel(){
			return "수위별";
		}
	},
	EVENT(){
		public String getLabel(){
			return "이벤트별";
		}
	},
	CASE(){
		public String getLabel(){
			return "상황별";
		}
	};
	public String getLabel(){
		return null;
	}
}
