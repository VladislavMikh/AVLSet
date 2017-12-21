import java.util.*;

public class AVLSet <T extends Comparable<T>> implements SortedSet<T> {

    private Node root;
    private int size = 0;

    private class Node {
        private T key;
        int height;
        Node left;
        Node right;

        Node (T key){
            this.key = key;
            height = 1;
        }

        private int getHeight() {
            return height;
        }

        private void recount() {
            int leftHeight = (left != null) ? left.getHeight() : 0;
            int rightHeight = (right != null) ? right.getHeight() : 0;
            this.height = ((leftHeight > rightHeight) ? leftHeight : rightHeight) + 1;
        }

        private int bFactor() {
            //Считаем разницу высот относительно правого поддрева
            int leftHeight = (left != null) ? left.getHeight() : 0;
            int rightHeight = (right != null) ? right.getHeight() : 0;
            return rightHeight - leftHeight;
        }

    }

    private Node turnLeft(Node n) {
        Node right = n.right;
        n.right = right.left;
        right.left = n;
        n.recount();
        right.recount();
        return right;
    }

    private Node turnRight(Node n) {
        Node left = n.left;
        n.left = left.right;
        left.right = n;
        n.recount();
        left.recount();
        return left;
    }

    private Node balance(Node n) {
        n.recount();
        if (n.bFactor() == 2) {
            if (n.right.bFactor() < 0)
                n.right = turnRight(n.right);
            return turnLeft(n);
        }
        if (n.bFactor() == -2) {
            if (n.left.bFactor() > 0)
                n.left = turnLeft(n.left);
            return turnRight(n);
        }
        return n;
    }

    private Node minimum(Node n) {
        if (n.left == null) return n;
        else return minimum(n.left);
    }

    private Node maximum(Node n) {
        if (n.right == null) return n;
        else return maximum(n.right);
    }

    private Node nextNode(Node n, Node prev, T key) {
        int comparison = key.compareTo(n.key);
        Node closest = (comparison < 0 && n.key.compareTo(prev.key) < 0) ? n : prev;
        if (comparison < 0 && n.left != null) {
            closest = nextNode(n.left, closest, key);
        } else if (comparison > 0 && n.right != null) {
            closest = nextNode(n.right, closest, key);
        } else if (comparison == 0){
            if (n.right != null) {
                return minimum(n.right);
            }
        }
        return closest;
    }

    private boolean contains(Node n, T key) {
        int comparison = key.compareTo(n.key);
        if (comparison < 0 && n.left != null) {
            return contains(n.left, key);
        } else if (comparison > 0 && n.right != null) {
            return contains(n.right, key);
        } else return comparison == 0;
    }

    private Node insert(Node n, T key) {
        int comparison = key.compareTo(n.key);
        if (comparison < 0) {
            if (n.left != null) {
                n.left = insert(n.left, key);
            } else {
                n.left = new Node(key);
                size++;
            }
        }
        if (comparison > 0) {
            if (n.right != null) {
                n.right = insert(n.right, key);
            } else {
                n.right = new Node(key);
                size++;
            }
        }
        return balance(n);
    }

    private Node removeMinimum(Node n) {
        if (n.left == null)
            return n.right;
        n.left = removeMinimum(n.left);
        return balance(n);
    }

    private Node remove(Node n, T key) {
        int comparison = key.compareTo(n.key);
        if (comparison < 0 && n.left != null) {
            n.left = remove(n.left ,key);
        } else if (comparison > 0 && n.right != null) {
            n.right = remove(n.right, key);
        } else if (comparison == 0) {
            size--;
            if (n.right == null) return n.left;
            Node swap = minimum(n.right);
            swap.right = removeMinimum(n.right);
            swap.left = n.left;
            return balance(swap);
        }
        return n;
    }

    private class TreeIterator implements Iterator<T> {
        Node first = null;
        Node current = null;
        Node last = null;
        boolean end = false;

        private TreeIterator() {
            if (root != null) {
                first = minimum(root);
                last = maximum(root);
            }
        }

        @Override
        public boolean hasNext() {
            return (current != null) ? (!end) : root != null;
        }

        @Override
        public T next() {
            Node next = (current == null) ? first : nextNode(root, last, current.key);
            if (end) throw new NoSuchElementException();
            end = (next == last);
            current = next;
            return next.key;
        }
    }

    @Override
    public Comparator<? super T> comparator() {
        return (Comparator<T>) Comparator.naturalOrder();
    }

    
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (!contains(fromElement) || !contains(toElement))
            throw new NoSuchElementException();
       AVLSet<T> subSet = new AVLSet<>();
       Iterator<T> iterator = new TreeIterator();
       while (iterator.hasNext()) {
           T i = iterator.next();
           if (i.compareTo(fromElement) >= 0 && i.compareTo(toElement) <= 0)
               subSet.add(i);
       }
       return subSet;
    }

    
    @Override
    public SortedSet<T> headSet(T toElement) {
        return subSet(first(), toElement);
    }

    
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return subSet(fromElement, last());
    }

    @Override
    public T first() {
        if (isEmpty()) throw new NoSuchElementException();
        return minimum(root).key;
    }

    @Override
    public T last() {
        if (isEmpty()) throw new NoSuchElementException();
        return maximum(root).key;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        return !isEmpty() && contains(root, t);
    }

    
    @Override
    public Iterator<T> iterator() {
        return new TreeIterator();
    }

    
    @Override
    public Object[] toArray() {
        Iterator<T> iterator = new TreeIterator();
        Object[] o = new Object[size];
        for (int i = 0; i < size; i++) {
            o[i] = iterator.next();
        }
        return o;
    }

    
    @Override
    @SuppressWarnings("unchecked")
    public <T1> T1[] toArray( T1[] a) {
        if (a.length < size)
            a = (T1[])java.lang.reflect.Array.newInstance(a.getClass(), size);
        Iterator<T> iterator = new TreeIterator();
        Object[] result = a;
        for (int i = 0; i < size; i++) {
            result[i] = iterator.next();
        }
        if (a.length > size)
            for (int i = size; i < a.length; i++)
                result[i] = null;
        return a;
    }

    @Override
    public boolean add(T t) {
        if (isEmpty()) {
            root = new Node(t);
            size++;
        }
        else root = insert(root, t);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        root = remove(root, t);
        return true;
    }

    @Override
    public boolean containsAll( Collection<?> c) {
        if (isEmpty() && c.size() > 0) return false;
        Object[] objects = c.toArray();
        for (Object object : objects) {
            @SuppressWarnings("unchecked")
            T t = (T) object;
            if (!contains(root, t)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll( Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean retainAll( Collection<?> c) {
        Node newRoot = null;
        Object[] elements = c.toArray();
        for (Object o : elements) {
            @SuppressWarnings("unchecked")
            T t = (T) o;
            if (contains(root, t)) {
                if (newRoot == null) newRoot = new Node(t);
                else insert(newRoot, t);
            }
        }
        root = newRoot;
        return true;
    }

    @Override
    public boolean removeAll( Collection<?> c) {
        c.forEach(E -> {
            @SuppressWarnings("unchecked")
            T t = (T) E;
            remove(t);
        });
        return true;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }
}
