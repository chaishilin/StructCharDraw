import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class TreeNodePlus extends TreeNode{
    public int count;//count记录当前节点是该层的第几个节点
    public TreeNodePlus(TreeNode node,int count){
        this.left = node.left;
        this.right = node.right;
        this.val = node.val;
        this.count = count;
    }
}

public class StructCharDraw {
    private static String emptyChar = " ";
    private static String leftChar = "{";
    private static String linkChar = "~";
    private static String rightChar = "}";
    private static String midChar = "^";

    /***
     *
     * @param text [1,null,2,3] 默认为中序遍历
     */
    public static void drawTree(String text) {
        TreeNode root = inorderRebuild(text);
        drawTree(root);
    }

    public static void drawTree(TreeNode root) {
        levelOrderTravel(root);
    }

    public static TreeNode inorderRebuild(String text) {
        return inorderRebuild(parseString(text));
    }

    public static TreeNode inorderRebuild(String[] inorder){
        //从中序遍历重建二叉树
        if(inorder.length == 0){
            return null;
        }
        TreeNode root = new TreeNode(Integer.parseInt(inorder[0]));
        List<TreeNode> queue = new ArrayList<TreeNode>();
        queue.add(root);

        int i = 1;
        while (i < inorder.length) {
            TreeNode temp = queue.remove(0);
            //取数组的下一个，连接temp的左边
            if (inorder[i].equals("null")) {
                i++;//如果数组中节点为空(null),则跳过该节点
            } else {
                temp.left = new TreeNode(Integer.parseInt(inorder[i++]));
                queue.add(temp.left);
            }

            if(i >= inorder.length) {
                break;
            }
            //取数组的下一个，连接temp的右边
            if (inorder[i].equals("null")) {
                i++;//如果数组中节点为空(null),则跳过该节点
            } else {
                temp.right = new TreeNode(Integer.parseInt(inorder[i++]));
                queue.add(temp.right);
            }
        }
        return root;
    }

    public static void printObjectList(Object[] objects){
        for(int k = 0; k < objects.length ; k+=1){
            System.out.print(objects[k]);
        }
        System.out.println("");
    }

    private static String[] parseString(String text) {
        text = text.replace('[', ' ');
        text = text.replace(']', ' ');
        text = text.trim();
        return text.split(",");
    }

    private static int getTreeHeight(TreeNode root) {
        if(root == null){
            return 0;
        }else if(root.left == null && root.right == null){
            return 1;
        }
        return 1+ Math.max(getTreeHeight(root.left),getTreeHeight(root.right));
    }

    private static int getPrintWidth(int height){
        int result = 1;
        for(int i = 1;i < height;i++){
            result = result*2+1;
        }
        return result;
    }

    private static int[] getABC(int index){
        int[] result = new int[3];
        result[0] = 0;
        result[1] = 3;
        result[2] = 1;
        for(int i = 2;i < index;i++){
            result[0] = result[0]*2+1;
            result[1] = 2*result[0]+3;
            result[2] = result[1] - 2;
        }
        return result;
    }

    private static void levelOrderTravel(TreeNode root){
        //中序遍历
        int height = getTreeHeight(root);
        int printLineWidth = getPrintWidth(height);
        List<TreeNodePlus> queue = new ArrayList<>();
        List<TreeNodePlus> queue2 = new ArrayList<>();
        queue.add(new TreeNodePlus(root,0));
        int level = 0;
        while(queue.size() > 0){
            //针对当前层数生成对应宽度的printList(1、2、4、8...)
            String[] printList = new String[(int) Math.pow(2,level)];
            for(int k = 0 ; k < printList.length;k++ ){
                printList[k] = emptyChar;
            }
            while (queue.size() > 0){
                //常规的中序遍历
                TreeNodePlus node = queue.remove(0);
                printList[node.count] = ""+node.val;
                if(node.left != null){
                    queue2.add(new TreeNodePlus(node.left,node.count*2));
                }
                if(node.right != null){
                    queue2.add(new TreeNodePlus(node.right,node.count*2+1));
                }
            }
            level += 1;
            List<TreeNodePlus> temp = queue;
            queue = queue2;
            queue2 = temp;
            printTreeLine(printList,printLineWidth,height,level);
        }
    }

    /***
     *
     * @param printList 仅含节点的打印行
     * @param printLineWidth 实际打印行宽度
     * @param height 树高度
     * @param level 当前节点高度
     */
    public static void printTreeLine(String[] printList,int printLineWidth,int height,int level){
        //将仅包含数字的printList和实际宽度传入，生成实际的打印行
        String[] numList = getPrintNumLine(printList,printLineWidth,level);
        if(printList.length > 1){
            //如果不是打印根节点，则需要打印树枝
            String[] branchList = getPrintBranchLine(height+2-level,printLineWidth);
            //根据数字层是否有数字修改树枝层
            branchList = changeBranchByNum(branchList,numList);
            printObjectList(branchList);
        }
        printObjectList(numList);
    }

    private static String[] changeBranchByNum(String[] branchList,String[] numList){
        //对于树枝层的leftChar和rightChar对应的数字层数字为null的情况，修改leftChar或rightChar为emptyChar
        //正反各一次即可
        int i = 0;
        while(i < branchList.length){
            if(branchList[i] == leftChar && numList[i] == emptyChar){
                while(branchList[i] != midChar){
                    branchList[i++] = emptyChar;
                }
            }else{
                i++;
            }
        }
        i--;
        while(i > 0){
            if(branchList[i] == rightChar && numList[i] == emptyChar){
                while(branchList[i] != midChar ){
                    branchList[i--] = emptyChar;
                }
            }else{
                i--;
            }
        }
        //消除单独的加号
        for(int j = 1; j < numList.length-1;j ++){
            if(branchList[j-1] == emptyChar && branchList[j] == midChar && branchList[j+1] == emptyChar){
                branchList[j] = emptyChar;
            }
        }
        return branchList;
    }

    private static String[] getPrintNumLine(String[] printList,int width,int level){
        String[] result = new String[width];
        int breakNum = width;
        while(level > 1){
            breakNum /= 2;
            level-=1;
        }
        int count = 0;
        int countP = 0;
        while(count < breakNum/2){
            result[count++] = emptyChar;
        }
        while(count < width){
            if(count%(breakNum+1) != breakNum/2){
                result[count++] = emptyChar;
            }else{
                result[count++] = printList[countP++];
            }
        }
        return result;
    }

    private static String[] getPrintBranchLine(int index,int width){
        //a:开始的空格数，b:/--+--\的长度，c:间隔长度
        int[] abc = getABC(index);
        String[] line = new String[width];
        int count = 0;
        while(count < abc[0]){
            line[count++] = emptyChar;
        }
        String[] paserdLine = paserBranchLine(abc[1],abc[2]);
        int countP = 0;
        while(count < width){
            line[count++] = paserdLine[countP];
            countP = (countP + 1)%paserdLine.length;
        }
        return line;
    }

    private static String[] paserBranchLine(int b,int c){
        String[] result = new String[b+c];
        for(int i = 0;i< b+c;i++){
            if(i < b){
                if(i == 0){
                    result[i] = leftChar;
                }else if(i == b-1){
                    result[i] = rightChar;
                }else if(i == b/2){
                    result[i] = midChar;
                }else{
                    result[i] =linkChar;
                }
            }else{
                result[i] = emptyChar;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        drawTree("[1,2,3,5,5,8,7,null,1,5,5,8,7,1,5,5,8,7,1,5,5,8,7,null,null,3,4,5,null,7,8]");
    }
}