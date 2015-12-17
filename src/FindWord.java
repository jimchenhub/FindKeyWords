import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindWord {
	private String path;
	private String key;
	
	public FindWord(String path, String key){
		this.path = path;
		this.key = key;
	}
	
	//搜索文件下下的所有文件
	public HashMap<String, ArrayList<String>> findWords() {
		HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		File file = new File(this.path);
		File[] tempList = file.listFiles();
//		System.out.println("该目录下对象个数："+tempList.length);
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
//				System.out.println("文件："+tempList[i]);
				ArrayList<String> data = getKeyContent(tempList[i].toString());
				if (data.size() > 0) {
					result.put(tempList[i].toString(), data);
				}
			}
		}
		return result;
	}
	
	//对每个文件进行处理
	public ArrayList<String> getKeyContent(String fileName) {
		// 获取文件内容
		File file = new File(fileName);
        BufferedReader reader = null;
        String content = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                content += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        
        //获取对应内容
        String[] tempList = key.split("or");
        ArrayList<String> result = new ArrayList<String>();
        for (String tempStr : tempList) {
        	ArrayList<String> tempResult = retrieveWord(tempStr, content);
        	if (tempResult.size() > 0) {
        		result.addAll(tempResult);
        	}
        }
        
		return result;
	}
	
	//根据关键字串对文件内容取对应片段
	public ArrayList<String> retrieveWord(String keyString, String content) {
		ArrayList<String> chooseWords = new ArrayList<String>();
        ArrayList<String> notChooseWords = new ArrayList<String>();
        
        String[] tempStrList = keyString.split("and");
        for (String str : tempStrList) {
        	if (str.contains("not")) {
        		String[] tempNotKeyList = str.split("not");
        		chooseWords.add(tempNotKeyList[0]);
        		for (int i = 1; i < tempNotKeyList.length; i++) {
        			notChooseWords.add(tempNotKeyList[i]);
        		}
        	} else {
        		chooseWords.add(str);
        	}
        }
        
		ArrayList<String> result = new ArrayList<String>();
		
		boolean flag = true;
		for (String str : chooseWords) {
			Pattern pattern = Pattern.compile("\\S{0,5}"+str+"\\S{0,5}", Pattern.MULTILINE | Pattern.DOTALL);
	        Matcher matcher = pattern.matcher(content);
	        flag = false;
	        while (matcher.find()) {
	        	flag = true;
	        	result.add(matcher.group());
	        }
	        if (!flag)
	        	break;
		}
		if (!flag) {
			result.clear();
			return result;
		}
        
		flag = false;
		for (String str : notChooseWords) {
			Pattern pattern = Pattern.compile("\\S{0,5}"+str+"\\S{0,5}", Pattern.MULTILINE | Pattern.DOTALL);
	        Matcher matcher = pattern.matcher(content);
	        if (matcher.find()) {
	        	flag = true;
	        	break;
	        }
		}
		if (flag) {
			result.clear();
		}
		
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("用法不正确，使用方法为 \n java FindWord [文件夹路径] [关键字串]\n");
			System.out.println("例如 java FindWord /use/local/ 中国and人民");
			return;
		}
		//获取输入
		String key = args[1];
		String path = args[0];
		
		//计算结果
		FindWord fw = new FindWord(path, key);
		HashMap<String, ArrayList<String>> result = fw.findWords();
		
		//输出结果
		Iterator<Entry<String, ArrayList<String>>> iter = result.entrySet().iterator();
		int count = 1;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next(); 
			String a = (String)entry.getKey();
			ArrayList<String> b = (ArrayList<String>)entry.getValue();
			
			System.out.println("文件"+count+"：" + a);
			for (String d : b) {
				System.out.println(d);
			}
			System.out.println("");
			count++;
		}
	}
}
