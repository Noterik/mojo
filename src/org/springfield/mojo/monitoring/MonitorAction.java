package org.springfield.mojo.monitoring;

import java.util.*;

public class MonitorAction {

	long startcounter=0;
	long endcounter=0;
	long totaltime = 0;
	String name;
	boolean firstcall = true;
	long[] trackers = null;
	int trackerid=0;
	int bucket =0;
	long sc0=0,sc1=0,sc2=0,sc3=0,sc4=0,sc5=0,sc6=0,sc7=0,sc8=0,sc9=0;
	long ec0=0,ec1=0,ec2=0,ec3=0,ec4=0,ec5=0,ec6=0,ec7=0,ec8=0,ec9=0;
	long tt0=0,tt1=0,tt2=0,tt3=0,tt4=0,tt5=0,tt6=0,tt7=0,tt8=0,tt9=0;

	public MonitorAction(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}
	
	public ArrayList<Long> getActiveList() {
		ArrayList<Long> list = new ArrayList<Long>();
		if (trackers!=null) {
			for (int i=0;i<10000;i++) {
				long value = trackers[i];
				if (value!=0) list.add(value);
			}
		}
		return list;
	}

	public long addStartCounter(boolean track) {

		if (firstcall) {
			firstcall = false;
			if (track) {
				trackers = new long[10000];
			}
			return -1;
		}
		long starttime = new Date().getTime();
		long token = starttime;
		token = token << 20;
		startcounter++; // add the overal counter
		// get the correct bucket based on startcounter
		int newbucket = ((int)(startcounter/10))%10;
		if (bucket!=newbucket) {
			bucket=newbucket;
			switch (bucket) {
			case 0 : sc0=1;ec0=0;tt0=0;break;
			case 1 : sc1=1;ec1=0;tt1=0;break;
			case 2 : sc2=1;ec2=0;tt2=0;break;
			case 3 : sc3=1;ec3=0;tt3=0;break;
			case 4 : sc4=1;ec4=0;tt4=0;break;
			case 5 : sc5=1;ec5=0;tt5=0;break;
			case 6 : sc6=1;ec6=0;tt6=0;break;
			case 7 : sc7=1;ec7=0;tt7=0;break;
			case 8 : sc8=1;ec8=0;tt8=0;break;
			case 9 : sc9=1;ec9=0;tt9=0;break;
			}
		} else {
			switch (bucket) {
			case 0 : sc0++;break;
			case 1 : sc1++;break;
			case 2 : sc2++;break;
			case 3 : sc3++;break;
			case 4 : sc4++;break;
			case 5 : sc5++;break;
			case 6 : sc6++;break;
			case 7 : sc7++;break;
			case 8 : sc8++;break;
			case 9 : sc9++;break;
			}
		}
		token+=bucket;
		if (track) {
			trackerid++;
			if (trackerid==10000) trackerid=0;
			trackers[trackerid] = starttime; // should we check for old failure?
			token+=(trackerid<<4);
		}
		return token;
	}

	public void logAction(long token) {
		if (token==-1) return; // was the first call ignore
		long startbucket =  token & 0xF;
		long trackingid =  token>>4;
		trackingid = trackingid & 0xFFFF;
		if (trackers!=null) trackers[trackerid] = 0;
		long starttime = token>>>20;
		long t=(new Date().getTime())-starttime;
		endcounter++; // a add is also a ending
		totaltime+=t;
		switch ((int)startbucket) {
		case 0 : ec0++;tt0+=t;break;
		case 1 : ec1++;tt1+=t;break;
		case 2 : ec2++;tt2+=t;break;
		case 3 : ec3++;tt3+=t;break;
		case 4 : ec4++;tt4+=t;break;
		case 5 : ec5++;tt5+=t;break;
		case 6 : ec6++;tt6+=t;break;
		case 7 : ec7++;tt7+=t;break;
		case 8 : ec8++;tt8+=t;break;
		case 9 : ec9++;tt9+=t;break;
		}
	}

	public long getStartCounter() {
		return startcounter;
	}

	public long getEndCounter() {
		return endcounter;
	}

	public long getAvgTime() {
		if (endcounter==0) return 0;
		return Math.round(totaltime/endcounter);
	}

	public String getBucketString() {
		String result = "";

		int roller = bucket;
		for (int i=0; i<10;i++) {
			switch (roller) {
			case 0 : 
				if (ec0==0) {
					result+=" 0/"+(sc0-ec0);
				} else {
					result+=" "+Math.round(tt0/ec0)+"/"+(sc0-ec0);
				}
				break;
			case 1 : 
				if (ec1==0) {
					result+=" 0/"+(sc1-ec1);
				} else {
					result+=" "+Math.round(tt1/ec1)+"/"+(sc1-ec1);
				}
				break;
			case 2 : 
				if (ec2==0) {
					result+=" 0/"+(sc2-ec2);
				} else {
					result+=" "+Math.round(tt2/ec2)+"/"+(sc2-ec2);
				}
				break;
			case 3 : 
				if (ec3==0) {
					result+=" 0/"+(sc3-ec3);
				} else {
					result+=" "+Math.round(tt3/ec3)+"/"+(sc3-ec3);
				}
				break;
			case 4 : 
				if (ec4==0) {
					result+=" 0/"+(sc4-ec4);
				} else {
					result+=" "+Math.round(tt4/ec4)+"/"+(sc4-ec4);
				}
				break;
			case 5 : 
				if (ec5==0) {
					result+=" 0/"+(sc5-ec5);
				} else {
					result+=" "+Math.round(tt5/ec5)+"/"+(sc5-ec5);
				}
				break;
			case 6 : 
				if (ec6==0) {
					result+=" 0/"+(sc6-ec6);
				} else {
					result+=" "+Math.round(tt6/ec6)+"/"+(sc6-ec6);
				}
				break;
			case 7 : 
				if (ec7==0) {
					result+=" 0/"+(sc7-ec7);
				} else {
					result+=" "+Math.round(tt7/ec7)+"/"+(sc7-ec7);
				}
				break;
			case 8 : 
				if (ec8==0) {
					result+=" 0/"+(sc8-ec8);
				} else {
					result+=" "+Math.round(tt8/ec8)+"/"+(sc8-ec8);
				}
				break;
			case 9 : 
				if (ec9==0) {
					result+=" 0/"+(sc9-ec9);
				} else {
					result+=" "+Math.round(tt9/ec9)+"/"+(sc9-ec9);
				}
				break;
			}
			roller = roller-1;
			if (roller==-1) roller=9;
		}

		return result;
	}
	
	public String getAvgTimesString() {
		String result = "";

		int roller = bucket;
		long t20=0;
		long t50=0;
		long t100=0;
		int f20=0;
		int f50=0;
		int f100=0;
		
		for (int i=0; i<10;i++) {
			switch (roller) {
			case 0 : 
				if (ec0==0) {
					// do nothing
				} else {
					long r=Math.round(tt0/ec0);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
				break;
			case 1 : 
				if (ec1==0) {
					// do nothing
				} else {
					long r=Math.round(tt1/ec1);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
				break;
			case 2 : 
				if (ec2==0) {
					// do nothing
				} else {
					long r=Math.round(tt2/ec2);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
				break;
			case 3 : 
				if (ec3==0) {
					// do nothing
				} else {
					long r=Math.round(tt3/ec3);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
				break;
			case 4 : 
				if (ec4==0) {
					// do nothing
				} else {
					long r=Math.round(tt4/ec4);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
				break;
			case 5 : 
				if (ec5==0) {
					// do nothing
				} else {
					long r=Math.round(tt5/ec5);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
				break;
			case 6 : 
				if (ec6==0) {
					// do nothing
				} else {
					long r=Math.round(tt6/ec6);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
				break;
			case 7 : 
				if (ec7==0) {
					// do nothing
				} else {
					long r=Math.round(tt7/ec7);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
				break;
			case 8 : 
				if (ec8==0) {
					// do nothing
				} else {
					long r=Math.round(tt8/ec8);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
				break;
			case 9 : 
				if (ec9==0) {
					// do nothing
				} else {
					long r=Math.round(tt9/ec9);
					if (f20<2) {
						t20+=r;
						f20++;
					}
					if (f50<5) {
						t50+=r;
						f50++;
					}
					f100++;
					t100+=r;
				}
			}
			roller = roller-1;
			if (roller==-1) roller=9;
		}
		if (f20==0) {
			result+="0";
		} else {
			result+=""+Math.round(t20/f20);
		}
		if (f50==0) {
			result+="/0";
		} else {
			result+="/"+Math.round(t50/f50);
		}
		if (f100==0) {
			result+="/0";
		} else {
			result+="/"+Math.round(t100/f100);
		}

		return result;
	}


}
