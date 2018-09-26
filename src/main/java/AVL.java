import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author panqiang37@gmail.com
 * @version kris37
 * Date: 2018/9/23 下午2:17
 * To change this template use File | Settings | File Templates.
 * Description:
 * <p>
 * <br>
 */
public class AVL<K extends Comparable,V> {

    private Node root;

    private  final class Node{
        private K key;
        private V value;
        private int size = 1;
        private int height = 0;
        private Node left;
        private Node right;
        public Node(K key,V value){
            this.key = key;
            this.value = value;
        }
    }

    // insert
    public void insert(K key,V value){
        if(Objects.isNull(key)){
            throw new IllegalArgumentException(" insert key is null !");
        }
        root  = insert(root,key,value);
    }

    /**
     *  添加元素 reBalance
     * @param node
     * @param key
     * @param value
     * @return
     */
    private Node insert(Node node,K key, V value){
        if(Objects.isNull(node)){
            return new Node(key,value);
        }
        int cmp = key.compareTo(node.key);
        if(cmp > 0){
            node.right =  insert(node.right,key,value);
        }else if (cmp < 0){
            node.left =  insert(node.left,key,value);
        }else {
            node.value = value;
        }

        node.height = reComputeHeight(node);
        node.size = reComputeSize(node);
        return rebalance(node);
    }

    public V search(K key){
        if (Objects.isNull(key)) return null;
        Node cur = root;
        while(cur != null){
            int cmp = key.compareTo(cur.key);
            if(cmp > 0){
                cur = cur.right;
            }else if(cmp < 0){
                cur =  cur.left;
            }else {
                return cur.value;
            }
        }
        return null;
    }


    public void delete(K key){
        if(Objects.isNull(key)) throw new IllegalArgumentException("delete key is null !");
        if(null == search(key)) return ;
        root = delete(root,key);
    }

    private Node delete(Node node,K key){
        int cmp = key.compareTo(node.key);
        if(cmp  > 0 ){
            node.right  = delete(node.right,key);
        }else if(cmp < 0){
            node.left =  delete(node.left,key);
        }else {

            if(node.left == null){
                return node.right;
            }else if(node.right == null){
                return node.left;
            }else {

                // left and right are't null。replace and delte minNode（）
                Node min = findMin(node.right);
                min.right = deleteMin(node.right);
                min.left = node.left;
                node = min;
            }
        }
        node.size = reComputeSize(node);
        node.height = reComputeHeight(node);
        return rebalance(node);
    }

    private Node findMin(Node node){
        while (node.left != null){
            node = node.left;
        }
        return node;
    }
    private Node deleteMin(Node node){
        if(node.left == null){
            return node.right;
        }
        node.left = deleteMin(node.left);
        node.size = reComputeSize(node);
        node.height = reComputeHeight(node);
        return rebalance(node);
    }


    /** 计算节点平衡因子
     *  compute node's balance factor
     * @param node
     * @return left.height - right.height
     */
    private int balanceFactor(Node node){
        return height(node.left) - height(node.right);
    }
    private int height(Node node){
        return node == null ? -1:node.height;
    }

    /**
     * 重新计算当前节点的高度
     * @param node
     * @return
     */
    private int reComputeHeight(Node node){
        return Math.max(height(node.left),height(node.right)) + 1;
    }

    private int size(Node node){
       return node == null ? 0: node.size;
    }

    /**
     * 重新计算当前节点的size
     * @param node
     * @return
     */
    private int reComputeSize(Node node){
        return size(node.left) + size(node.right) + 1;
    }
    /**
     *  对非平衡节点进行调整
     * @param node
     * @return
     *
     */
    private Node rebalance(Node node){
        int factor = balanceFactor(node);

        if(factor > 1){
            if(balanceFactor(node.left) < 0){
                node.left = leftRotate(node.left);
            }
            node = rightRotate(node);
        }else if (factor < -1){
            if(balanceFactor(node.right) > 0){
                node.right = rightRotate(node.right);
            }
            node = leftRotate(node);
        }
        return node;
    }

    /**
     *  左旋
     *
     *      x               y
     *     ／\              /\
     *     ∂ y     ==>     x ç
     *      / \           /\
     *      ß  ç         ∂ ß
     *
     * @param x
     * @return
     */
    private Node leftRotate(Node x){
        if( x == null||x.right == null)
            throw new IllegalArgumentException("left rotate node or node.right is null !");
        Node y = x.right;
        x.right = y.left;
        y.left = x;
        x.size = reComputeSize(x);
        y.size = reComputeSize(y);
        x.height = reComputeHeight(x);
        y.height = reComputeHeight(y);
        return y;
    }

    private Node rightRotate(Node x){
        Node y = x.left;
        x.left = y.right;
        y.right = x;
        x.size = reComputeSize(x);
        y.size = reComputeSize(y);
        x.height = reComputeHeight(x);
        y.height = reComputeHeight(y);
        return y;
    }

    /**
     * inorder lookup
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

    /**
     * AVL tree's height ,begin from 0; if null height = -1;
     * @return
     */
    public int height(){
        return height(root);
    }
    /**
     * AVL 树的节点数
     * @return
     */
    public int size(){
        return size(root);
    }


    /**
     *  查找排名第n的元素（min -> max）
     * @param n
     * @return
     */
    public Node select(int n){
        if (n > size(root) || n < 1){
            return null;
        }
        return select(root,n);
    }
    private Node select(Node node,int n){

        int rank = size(node.left) + 1;
        if(n > rank){
            return select(node.right,n -rank);
        }else if(n < rank){
            return select(node.left,n);
        }else {
            return node;
        }
    }

    /**
     *  查看当前 k 的排名
     * @param key
     * @return
     */
    public int rank(K key){
        if(Objects.isNull(key)){
            throw new IllegalArgumentException(" rank key is null !");
        }
        return rank(root,key);

    }

    private int rank(Node node,K key){

        int cmp = 0;
        int rank = 0;
        while (node != null){
            cmp = key.compareTo(node.key);
            if(cmp > 0){
                rank = rank + size(node.left) + 1;
                node = node.right;
            }else if(cmp < 0){
                node = node.left;
            }else {
                rank = rank + size(node.left) + 1;
                return rank;
            }
        }
        return -1;
    }

    public static void main(String []args){

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


        for (int i = 1;i<avl.size();i++){
            int key = avl.select(i).key;
            System.out.println(String.format("select rank = %d  key is: ",i)+ key );
            System.out.println(String.format("select key = %d  rank is: ",key)+ avl.rank(key) );

        }

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
