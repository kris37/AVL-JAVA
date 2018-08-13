
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author panqiang37@gmail.com
 * @version JDK8
 * Date: 2018/8/10 下午5:44
 * To change this template use File | Settings | File Templates.
 * Description:
 * <p>
 *     JAVA AVL树基础操作
 *
 * <br>
 */
public class AVL<K extends Comparable,V> implements Serializable{

    private static final long serialVersionUID = -7958565643636780390L;

    private Node root;
    public AVL(){

    }

    private class Node{
        private K key;
        private V value;
        private int height;
        private int size;
        private Node left;
        private Node right;
        public Node(K key,V value,int height,int size){
            this.height = height;
            this.size = size;
            this.key = key;
            this.value = value;
        }

    }

    /**
     * AVL 树的节点数
     * @return
     */
    public int size(){
        return size(root);
    }

    /**
     *  node 为parent 的子树节点数
     * @param node
     * @return
     */
    private int size(Node node){
        if(Objects.isNull(node)){
            return 0;
        }else{
            return node.size;
        }
    }

    /**
     * AVL tree's height ,begin from 0; if null height = -1;
     * @return
     */
    public int height(){
        return height(root);
    }

    private int height(Node node){
        if(Objects.isNull(node)){
            return -1;
        }
        return node.height;
    }

    // avl 树的增删改查

    /**
     * search
     */

    public Node get(K key){
        return get(root,key);
    }

    private Node get(Node node,K key){
        if(Objects.isNull(key)){
            throw new IllegalArgumentException("accept key is null");
        }
        if(Objects.isNull(node)){
            return null;
        }
        int cmp = key.compareTo(node.key);
        if(cmp > 0){
            return get(node.right,key);
        }else if(cmp < 0 ){
            return get(node.left,key);
        }else{
            return node;
        }
    }

    public boolean contains(K key){
        return contains(root,key);
    }
    private boolean contains(Node node,K key){
        return !Objects.isNull(get(node,key));
    }

    public void insert(K key,V value){
        root = insert(root,key,value);
    }

    private Node insert(Node node,K key,V value){
        if(Objects.isNull(node)){
            return new Node(key,value,0,1);
        }
        int cmp = key.compareTo(node.key);
        if(cmp > 0 ){
            node.right = insert(node.right,key,value);
        }else if(cmp < 0){
            node.left = insert(node.left,key,value);
        }else{
            node.value = value;
            return node;
        }
        node.height = Math.max(height(node.left),height(node.right)) + 1;
        node.size = size(node.left) + size(node.right) + 1;
        return reBalance(node);
    }


    /**
     * 自平衡
     * @param node
     * @return
     */
    private Node reBalance(Node node){
        int factor = balanceFactor(node);
        // 左轻右重
        if(factor < -1){
            // RL模式
            if(balanceFactor(node.right) > 0){
                node.right = rightRotate(node.right);
            }
            node = leftRotate(node);
        }else if(factor > 1){
            //
            if(balanceFactor(node.left) < 0){
                node.left = leftRotate(node.left);
            }
            node = rightRotate(node);
        }
        return node;
    }

    private int balanceFactor(Node node){
        return height(node.left ) - height(node.right);
    }


    /**
     * node 节点转换为左节点
     * @param node
     * @return
     */
    private Node leftRotate(Node node){
        Node subRoot = node.right;
        node.right = subRoot.left;
        subRoot.left = node ;
        node.size = size(node.left) + size(node.right) + 1;
        node.height =  Math.max(height(node.left),height(node.right)) + 1;
        subRoot.size = size(subRoot.left) + size(subRoot.right) + 1;
        subRoot.height = Math.max(height(subRoot.left),height(subRoot.right)) + 1;
        return subRoot;
    }

    /**
     * node 节点转化为右节点
     * @param node
     * @return
     */
    private Node rightRotate(Node node){
        Node subroot = node.left;
        node.left = subroot.right;
        subroot.right = node;
        node.size = size(node.left) + size(node.right) + 1;
        node.height =  Math.max(height(node.left),height(node.right)) + 1;
        subroot.size = size(subroot.left) + size(subroot.right) + 1;
        subroot.height = Math.max(height(subroot.left),height(subroot.right)) + 1;
        return subroot;
    }


    /**
     * 中序遍历
     */
    public List<K> inOrder(){
        List list = new ArrayList<K>();
        inOrder(root,list);
        return list;

    }

    private List inOrder(Node node,List<K> list){
        if(Objects.isNull(node)){
            return list;
        }
        if(!Objects.isNull(node.left)){
            inOrder(node.left,list);
        }
        list.add(node.key);
        if(!Objects.isNull(node.right)){
            inOrder(node.right,list);
        }
        return list;
    }

    // delete

    /**
     * 删除 node节点
     * @param key
     */
    public void delete(K key){
        // 不包含则直接返回
        if(!contains(key)) return;
        root = delete(root,key);
    }

    /**
     *  删除节点
     *  1。此节点是 leaf节点 直接删除
     *  2。此节点的 左／右节点为 null 则 node.parent -> node.right/or null
     *  3. 此节点的左右节点都不为null
     *   3.1 将左子树的最大node 替换掉当前node
     *   3.2 右子树的最小node 替换当前node
     *
     * 逐层递归更新 size height rebalance
     * @param node
     * @param key
     * @return
     */
    private Node delete(Node node,K key){
        if(Objects.isNull(node)){
            return null;
        }
        int cmp = key.compareTo(node.key);

        if(cmp > 0){
            node.right = delete(node.right,key);
        }else if(cmp < 0){
            node.left = delete(node.left,key);
        }else{
            // 找到节点，准备进行删除
            if(node.height == 0){
                //leaf
                return null;
            }
            if(Objects.isNull(node.left)){
                return node.right;
            }
            if(Objects.isNull(node.right)){
                return node.left;
            }
            // 左右节点都存在
            // 这里比较一下左右节点的height 选择height 高的进行处理
            Node temp = node;
            if(node.left.height - node.right.height > 0){
                node = findMaxNode(temp.left);
                node.left = deteleMaxNode(temp.left);
                node.right = temp.right;
            }else {
                node = findMinNode(temp.right);
                node.right = deteleMinNode(temp.right);
                node.left = temp.left;
            }
        }
        node.height = Math.max(height(node.left),height(node.right)) + 1;
        node.size = size(node.left) + size(node.right) + 1 ;
        return reBalance(node);
    }


    private Node deteleMaxNode(Node node){
        if(Objects.isNull(node.right)){
            return node.left;
        }else{
            node.right = deteleMaxNode(node.right);
        }
        node.height = Math.max(height(node.left),height(node.right)) + 1;
        node.size = size(node.left) + size(node.right) + 1;
        return reBalance(node);
    }

    private Node deteleMinNode(Node node){
        if(Objects.isNull(node.left)){
            return node.right;
        }else{
            node.left = deteleMinNode(node.left);
        }
        node.height = Math.max(height(node.left),height(node.right)) + 1;
        node.size = size(node.left) + size(node.right) + 1;
        return reBalance(node);

    }

    private Node findMaxNode(Node node){
        if(Objects.isNull(node.right)){
            return node;
        }else{
            return findMaxNode(node.right);
        }
    }

    private Node findMinNode(Node node){

        if(Objects.isNull(node.left)){
            return node;
        }else{
            return findMinNode(node.left);
        }
    }

    public static void main(String [] args){

        List<Integer> list = Arrays.asList(3,4,2,1,4,6,3,6,8,4,6,7,2,4,310,23,35,2,34);
        AVL<Integer, Integer> avl = new AVL<>();
        list.forEach(each -> {
            avl.insert(each,each);
            List<Integer> nodes = avl.inOrder();
            for (Integer node:nodes
                    ) {
                System.out.print(node + " -> ");
            }
            System.out.println(" height: "+ avl.height() + " size: "+ avl.size());
        });

        list.forEach(each -> {
            avl.delete(each);
            List<Integer> nodes = avl.inOrder();
            for (Integer node:nodes
                    ) {
                System.out.print(node + " -> ");
            }
            System.out.println(" height: "+ avl.height() + " size: "+ avl.size());
        });


    }
}


