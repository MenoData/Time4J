/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZoneLabels.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.format.expert;

import net.time4j.tz.TZID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Immutable ternary search trie holding zone labels.
 *
 * @author  Meno Hochschild
 * @since   3.35/4.30
 */
class ZoneLabels {

    //~ Instanzvariablen --------------------------------------------------

    private final Node root;

    //~ Konstruktoren -----------------------------------------------------

    ZoneLabels(Node root) {
        super();

        this.root = root;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * For debugging purposes.
     *
     * @return  String
     */
    @Override
    public String toString() {

        List<String> keys = new ArrayList<String>();
        this.collect(this.root, new StringBuilder(), keys);
        StringBuilder sb = new StringBuilder();
        sb.append("count=");
        sb.append(keys.size());
        sb.append(",labels={");
        for (String key : keys) {
            sb.append(key);
            sb.append("=>");
            sb.append(this.find(key));
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1).append('}');
        return sb.toString();

    }

    static Node insert(
        Node node,
        String key,
        TZID zoneID
    ) {

        if (key.isEmpty()) {
            throw new IllegalArgumentException("Empty key cannot be inserted.");
        } else if (zoneID == null) {
            throw new NullPointerException("Missing timezone id.");
        }

        return insert(node, key, zoneID, 0);

    }

    String longestPrefixOf(
        CharSequence query,
        int offset
    ) {

        int len = offset;
        Node node = this.root;
        int i = offset;
        int n = query.length();

        while ((node != null) && (i < n)) {
            char c = query.charAt(i);

            if (c < node.c) {
                node = node.left;
            } else if (c > node.c) {
                node = node.right;
            } else {
                i++;
                if (node.zoneIDs != null) { // end node condition
                    len = i;
                }
                node = node.mid;
            }
        }

        return ((offset >= len) ? "" : query.subSequence(offset, len).toString());

    }

    List<TZID> find(String key) {

        if (key.isEmpty()) {
            return Collections.emptyList();
        }

        Node node = find(this.root, key, 0);

        if (node == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(node.zoneIDs);
        }

    }

    private static Node find(
        Node node,
        String key,
        int pos
    ) {

        if (node == null) {
            return null;
        }

        char c = key.charAt(pos);

        if (c < node.c) {
            return find(node.left, key, pos);
        } else if (c > node.c) {
            return find(node.right, key, pos);
        } else if (pos < key.length() - 1) {
            return find(node.mid, key, pos + 1);
        } else {
            return node;
        }

    }

    private static Node insert(
        Node node,
        String key,
        TZID zoneID,
        int pos
    ) {

        char c = key.charAt(pos);

        if (node == null) {
            node = new Node(c);
        }

        if (c < node.c) {
            node = node.withLeft(insert(node.left, key, zoneID, pos));
        } else if (c > node.c) {
            node = node.withRight(insert(node.right, key, zoneID, pos));
        } else if (pos < key.length() - 1) {
            node = node.withMid(insert(node.mid, key, zoneID, pos + 1));
        } else {
            node = node.with(zoneID); // end node
        }

        return node;

    }

    private void collect(
        Node node,
        StringBuilder prefix,
        List<String> keys
    ) {

        if (node == null) {
            return;
        }

        collect(node.left, prefix, keys);

        if (node.zoneIDs != null) {
            keys.add(prefix.toString() + node.c);
        }

        collect(node.mid, prefix.append(node.c), keys);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(node.right, prefix, keys);

    }

    //~ Innere Klassen ----------------------------------------------------

    static class Node {

        //~ Instanzvariablen ----------------------------------------------

        private final char c;
        private final Node left;
        private final Node mid;
        private final Node right;
        private final List<TZID> zoneIDs;

        //~ Konstruktoren -------------------------------------------------

        private Node(char c) {
            this(c, null, null, null, null);

        }

        private Node(char c, Node left, Node mid, Node right, List<TZID> zoneIDs) {
            super();

            this.c = c;
            this.left = left;
            this.mid = mid;
            this.right = right;
            this.zoneIDs = zoneIDs;

        }

        //~ Methoden ------------------------------------------------------

        private Node withLeft(Node left) {

            return new Node(this.c, left, this.mid, this.right, this.zoneIDs);

        }

        private Node withMid(Node mid) {

            return new Node(this.c, this.left, mid, this.right, this.zoneIDs);

        }

        private Node withRight(Node right) {

            return new Node(this.c, this.left, this.mid, right, this.zoneIDs);

        }

        private Node with(TZID zoneID) { // end node

            List<TZID> zoneIDs = new ArrayList<TZID>();

            if (this.zoneIDs != null) {
                zoneIDs.addAll(this.zoneIDs);
            }

            zoneIDs.add(zoneID);
            return new Node(this.c, this.left, this.mid, this.right, zoneIDs);

        }

    }

}
