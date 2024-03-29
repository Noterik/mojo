package org.springfield.fs;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class FsEncoding {
	
	public FsEncoding() {}
	
	public static String decode(String input) {
		if (input == null) return null;
		if (input.indexOf("\\")!=-1) {
			int pos = input.indexOf("\\");
			StringBuffer output = new StringBuffer("");
			while (pos!=-1) {
				// is it dec or hex encoding ?
				if (input.charAt(pos+2) == 'x') {
					output.append(input.substring(0,pos));
					int code = Integer.parseInt(input.substring(pos+3,pos+7),16);
					output.append((char)code);
					input = input.substring(pos+7);
				} else {
					output.append(input.substring(0,pos));
					try {
						int code = Integer.parseInt(input.substring(pos+1,pos+4));
						output.append((char)code);
						input = input.substring(pos+4);
					} catch(Exception e) {
						//seems like we can't parse the character, treat as normal character \
						//this prevent infinite loops
						output.append(input.substring(0, 1));
						input = input.substring(pos+1);
						System.out.println("POS="+pos+"S="+input.substring(pos+1,pos+4)+" E="+input);
						//e.printStackTrace();
					}

				}
				pos = input.indexOf("\\");
			}
			output.append(input);
			String outs=output.toString();
			//System.out.println("decode out="+outs);
			outs = fixMalformedUTF(outs);
			
			return outs;
		} else {
			return input;
		}
	}	
	
	public static String encode(String input) {
		input = fixMalformedUTF(input);
		
		StringBuffer output = new StringBuffer("");
		for (int i = 0; i < input.length(); i++) {
			int code = (int) input.charAt(i);
			if (code > 127 && code < 1000) {
				output.append("\\"+code);
			} else if (code==13) {
				output.append("\\013");
			} else {
				if (code==37) {
						output.append("\\037");
				} else if (code==35) {
						output.append("\\035");
				} else if (code==61) {
					output.append("\\061");
				} else if (code==92) {
						output.append("\\092");
				}  
				else {
						if (code>999) {
							//System.out.println("character = "+ input.charAt(i) +" code = "+code);
							String t = Integer.toHexString(code);
							if (t.length() == 2) {
								output.append("\\0x00"+Integer.toHexString(code));
							} else if (t.length() == 3) {
									output.append("\\0x0"+Integer.toHexString(code));
							} else {
								output.append("\\0x"+Integer.toHexString(code));		
							}
						} else {
							output.append(input.charAt(i));	
						}
				}
			}
		}
		return output.toString();
	}
	
	public static String fixMalformedUTF(String input) {
		CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
		decoder.onMalformedInput(CodingErrorAction.IGNORE);
        decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        
        String output = input;
        
        try {
        
        	output = decoder.decode(ByteBuffer.wrap(input.getBytes("UTF-8"))).toString();
        } catch (CharacterCodingException e) {
        	System.out.println("Character coding exception on: "+input);
        } catch (UnsupportedEncodingException e) {
        	System.out.println("Unsupported coding exception on: "+input);
        }
        
        return output;
	}
 }
